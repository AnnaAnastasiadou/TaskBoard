package com.example.taskboard.presentation.posts.list

import com.example.taskboard.data.dummyPostList
import com.example.taskboard.data.dummyPostResponse
import com.example.taskboard.data.dummyPostsEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.response.PostResponse
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Dispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostsViewModelTest {

    private val postsRepository: PostsRepository = mockk()
    private val networkMonitor: NetworkMonitor = mockk()
    private lateinit var viewModel: PostsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNextBatch prevents concurrent execution via isFetching flag`() = runTest {
        // Verify that if loadNextBatch is called while a previous execution is still in progress (isFetching = true),
        // the function returns immediately and does not trigger a second repository call.
        val deferredResult = CompletableDeferred<NetworkResult<PostResponse>>()

        coEvery { postsRepository.refreshPosts(any(), any()) } coAnswers { deferredResult.await() }
        every { postsRepository.observeLocalPosts() } returns emptyFlow()
        every { postsRepository.observeRemotePosts() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = PostsViewModel(postsRepository, networkMonitor)

        // make sure that the viewModel coroutine inside the loadNextBatch runs
        // in runTest coroutines need to be specifically told to run
        runCurrent()

        // These calls should be blocked by isFetching = true
        viewModel.loadNextBatch()
        viewModel.loadNextBatch()

        // verify there was only one call to refresh the posts
        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }

        // the ui should show loading state
        assertTrue(viewModel.uiState.value.isLoading)

        // Provide the success data to the suspended coroutine (unlocking the await call)
        deferredResult.complete(NetworkResult.Success(data = dummyPostResponse))
        // We use runCurrent here because advanceUntilIdle will wait for
        // any infinite loops in the ViewModelScope to finish.
        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadNextBatch updates currentSkip correctly on NetworkResult Success`() = runTest {
        // Ensure that currentSkip increments by exactly pageSize (30) after a successful repository refresh,
        // enabling correct pagination for subsequent calls.

        coEvery { postsRepository.refreshPosts(any(), any()) } returns NetworkResult.Success(
            dummyPostResponse
        )
        every { postsRepository.observeLocalPosts() } returns emptyFlow()
        every { postsRepository.observeRemotePosts() } returns flowOf(dummyPostsEntity)
        every { networkMonitor.isOnline } returns emptyFlow()

        // This triggers the first call (skip = 0) via the init block
        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        coVerify { postsRepository.refreshPosts(30, 0) }

        viewModel.loadNextBatch()
        runCurrent()

        coVerify { postsRepository.refreshPosts(30, 30) }

        viewModel.loadNextBatch()
        runCurrent()

        coVerify { postsRepository.refreshPosts(30, 60) }

        assertEquals(dummyPostList, viewModel.uiState.value.remoteData)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadNextBatch sets totalItems only on the first successful fetch`() = runTest {
        // Check that totalItems is initialized with the value from result.data.total when it is currently null,
        // and verify it is not overwritten by subsequent batch results.
        val firstResponse = dummyPostResponse.copy(total = 2)
        val secondResponse = dummyPostResponse.copy(total = 100)
        val thirdResponse = dummyPostResponse.copy(total = 90)

        coEvery { postsRepository.refreshPosts(any(), any()) } returnsMany listOf(
            NetworkResult.Success(firstResponse),
            NetworkResult.Success(secondResponse),
            NetworkResult.Success(thirdResponse)
        )
        every { postsRepository.observeLocalPosts() } returns emptyFlow()
        every { postsRepository.observeRemotePosts() } returns flowOf(dummyPostsEntity)
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        viewModel.loadNextBatch()
        runCurrent()

        viewModel.loadNextBatch()
        runCurrent()

        // clear the history of calls on postsRepository, but do not erase their results
        clearMocks(postsRepository, answers = false, recordedCalls = true)

        // Total data should be 2 and hasNext should be false
        viewModel.onScrollReachedIndex(2)
        runCurrent()

        // there shouldn't be any new calls to refresh posts since the last page has been reached
        coVerify(exactly = 0) { postsRepository.refreshPosts(any(), any()) }

    }

    @Test
    fun `loadNextBatch handles NetworkResult Error by updating UI state error`() = runTest {
        // Verify that a repository error result sets the error message in UI state, resets isLoading to false,
        // and ensures networkError is null.
        val errorMessage = "Internal Server Error"
        coEvery { postsRepository.refreshPosts(any(), any()) } returns NetworkResult.Error(
            errorMessage
        )

        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { postsRepository.observeRemotePosts() } returns flowOf(emptyList())
        every { networkMonitor.isOnline } returns flowOf(true)

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.networkError)
    }

    @Test
    fun `loadNextBatch recovers from NetworkError when isOnline emits true`() = runTest {
        val errorMessage = "Network Error"
        val networkStatus = MutableStateFlow(false)
        coEvery { postsRepository.refreshPosts(any(), any()) } returnsMany listOf(
            NetworkResult.NetworkError(errorMessage),
            NetworkResult.Success(dummyPostResponse)
        )
        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { postsRepository.observeRemotePosts() } returns flowOf(emptyList())
        every { networkMonitor.isOnline } returns networkStatus

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        assertEquals(errorMessage, viewModel.uiState.value.networkError)
        assertNull(viewModel.uiState.value.error)

        clearMocks(postsRepository, answers = false)
        networkStatus.value = true
        runCurrent()
        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }

        assertNull(viewModel.uiState.value.networkError)
    }

    @Test
    fun `loadNextBatch resets isFetching flag even after failure`() = runTest {
        // Verify that isFetching is set back to false at the end of the coroutine scope regardless
        // of whether the result was Success, Error, or NetworkError.
        val errorMessage = "First Attempt Failed"
        coEvery { postsRepository.refreshPosts(any(), any()) } returns
                NetworkResult.Error(errorMessage)

        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { postsRepository.observeRemotePosts() } returns flowOf(emptyList())
        every { networkMonitor.isOnline } returns flowOf(true)

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }
        assertEquals(errorMessage, viewModel.uiState.value.error)

        coEvery { postsRepository.refreshPosts(any(), any()) } returns
                NetworkResult.Success(dummyPostResponse)

        viewModel.loadNextBatch()
        runCurrent()

        coVerify(exactly = 2) { postsRepository.refreshPosts(any(), any()) }
        assertNull(viewModel.uiState.value.error)
    }


    @Test
    fun `onScrollReachedIndex ignores calls if an error state currently exists`() = runTest {
        // Ensure loadNextBatch is not triggered when scrolling if either 'error' or 'networkError'
        // in the UI state is non-null, preventing infinite retry loops during scroll.
        val errorMessage = "Server Down"
        coEvery { postsRepository.refreshPosts(any(), any()) } returnsMany listOf(
            NetworkResult.Success(dummyPostResponse.copy(total = 100)), // totalItems becomes 100
            NetworkResult.Error(errorMessage)
        )

        every { postsRepository.observeRemotePosts() } returns flowOf(dummyPostsEntity)
        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { networkMonitor.isOnline } returns flowOf(true)

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        viewModel.loadNextBatch()
        runCurrent()

        clearMocks(postsRepository, answers = false, recordedCalls = true)

        viewModel.onScrollReachedIndex(2)
        runCurrent()

        coVerify(exactly = 0) { postsRepository.refreshPosts(any(), any()) }
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }

    @Test
    fun `onScrollReachedIndex detects end of list based on local and remote data size`() = runTest {
        // Validate the hasNext calculation: if (localItems + remoteItems) equals or exceeds totalItems,
        // loadNextBatch should not be called.
        val apiTotal = 2
        val firstResponse = dummyPostResponse.copy(
            posts = listOf(
                dummyPostResponse.posts[1],
            ), total = apiTotal
        )
        val totalResponse = dummyPostResponse.copy(
            posts = listOf(
                dummyPostResponse.posts[1],
                dummyPostResponse.posts[2]
            ), total = apiTotal
        )

        coEvery { postsRepository.refreshPosts(any(), any()) } returnsMany listOf(
            NetworkResult.Success(firstResponse),
            NetworkResult.Success(totalResponse)
        )

        every { postsRepository.observeLocalPosts() } returns flowOf(listOf(dummyPostsEntity[0]))

        val remoteFlow = MutableStateFlow(listOf(dummyPostsEntity[1]))
        every { postsRepository.observeRemotePosts() }  returns remoteFlow
        every { networkMonitor.isOnline } returns flowOf(true)

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        clearMocks(postsRepository, answers = false, recordedCalls = true)

        viewModel.onScrollReachedIndex(2)
        runCurrent()
        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }
        remoteFlow.value = listOf(dummyPostsEntity[1], dummyPostsEntity[2])
        runCurrent()

        viewModel.onScrollReachedIndex(5)
        runCurrent()
        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }

    }

    @Test
    fun `onScrollReachedIndex accounts for dynamic headers in total count`() = runTest {
        // This test proves that if we have local items, the count includes the local header
        val local = listOf(dummyPostsEntity[0])
        val remote = List(10) { dummyPostsEntity[1] }
        // Total = 1 (L) + 10 (R) + 1 (LH) + 1 (RH) = 13 items.
        // Threshold = 13 - 5 = 8.
        coEvery { postsRepository.refreshPosts(any(), any()) } returns
                NetworkResult.Success(dummyPostResponse.copy(total = 90))
        every { postsRepository.observeLocalPosts() } returns flowOf(local)
        every { postsRepository.observeRemotePosts() } returns flowOf(remote)
        every { networkMonitor.isOnline } returns flowOf(true)
        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()
        clearMocks(postsRepository, answers = false, recordedCalls = true)

        // If headers weren't counted, total would be 11, threshold would be 6.
        // By checking index 7 doesn't trigger, we prove headers are included.
        viewModel.onScrollReachedIndex(7)
        runCurrent()
        coVerify(exactly = 0) { postsRepository.refreshPosts(any(), any()) }

        viewModel.onScrollReachedIndex(8)
        runCurrent()
        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }
    }

    @Test
    fun `onScrollReachedIndex triggers loadNextBatch within the 5 item threshold`() = runTest {
        // Test that loadNextBatch is called when the index is exactly totalItems - 5,
        // ensuring pre-fetching occurs before the user hits the bottom.
        val remoteFlow = MutableStateFlow(List(20) { dummyPostsEntity[1] })
        // Total = 0 (L) + 20 (R) + 0 (LH) + 1 (RH) = 21 items.
        // Threshold = 21 - 5 = 16.

        coEvery { postsRepository.refreshPosts(any(), any()) } returns
                NetworkResult.Success(dummyPostResponse.copy(total = 90))
        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { postsRepository.observeRemotePosts() } returns remoteFlow
        every { networkMonitor.isOnline } returns flowOf(true)
        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()
        clearMocks(postsRepository, answers = false, recordedCalls = true)

        viewModel.onScrollReachedIndex(16)
        runCurrent()
        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }
        remoteFlow.value = List(40) { dummyPostsEntity[1] }
        runCurrent()

        viewModel.onScrollReachedIndex(36)
        runCurrent()
        coVerify(exactly = 2) { postsRepository.refreshPosts(any(), any()) }

    }

    @Test
    fun `onScrollReachedIndex does not trigger loadNextBatch if index is outside threshold`() = runTest {
        val remoteFlow = MutableStateFlow(List(20) { dummyPostsEntity[1] })

        coEvery { postsRepository.refreshPosts(any(), any()) } returns
                NetworkResult.Success(dummyPostResponse.copy(total = 90))
        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { postsRepository.observeRemotePosts() } returns remoteFlow
        every { networkMonitor.isOnline } returns flowOf(true)

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        runCurrent()

        clearMocks(postsRepository, answers = false, recordedCalls = true)

        viewModel.onScrollReachedIndex(0)
        runCurrent()
        viewModel.onScrollReachedIndex(7)
        runCurrent()
        viewModel.onScrollReachedIndex(15)
        runCurrent()

        coVerify(exactly = 0) { postsRepository.refreshPosts(any(), any()) }
    }

    @Test
    fun `onScrollReachedIndex respects isFetching state`() = runTest {
        // Verify that if a fetch is already in progress, reaching the scroll index threshold
        // does not trigger redundant calls to loadNextBatch.
        val remoteFlow = MutableStateFlow(List(20) { dummyPostsEntity[1] })
        coEvery { postsRepository.refreshPosts(any(), any()) } coAnswers {
            delay(1000) // Simulate network latency
            NetworkResult.Success(dummyPostResponse.copy(total = 90))
        }
        every { postsRepository.observeLocalPosts() } returns flowOf(emptyList())
        every { postsRepository.observeRemotePosts() } returns remoteFlow
        every { networkMonitor.isOnline } returns flowOf(true)

        viewModel = PostsViewModel(postsRepository, networkMonitor)
        advanceTimeBy(1000)
        runCurrent()
        clearMocks(postsRepository, answers = false, recordedCalls = true)

        // Trigger the first scroll (Threshold is 16)
        viewModel.onScrollReachedIndex(16)
        runCurrent()
        advanceTimeBy(500)
        // Trigger again while the first one is still "fetching" (at 500ms)
        viewModel.onScrollReachedIndex(17)
        advanceTimeBy(100)
        viewModel.onScrollReachedIndex(18)
        runCurrent()

        coVerify(exactly = 1) { postsRepository.refreshPosts(any(), any()) }
        advanceTimeBy(500)
        runCurrent()
    }
}