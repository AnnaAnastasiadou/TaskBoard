package com.example.taskboard.presentation.todos.list

import app.cash.turbine.test
import com.example.taskboard.data.dummyPostResponse
import com.example.taskboard.data.dummyPostsEntity
import com.example.taskboard.data.dummyTodoListDto
import com.example.taskboard.data.dummyTodoListEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.dummyTodosResponse
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodosViewModelTest {
    private val todosRepository: TodosRepository = mockk()
    private val networkMonitor: NetworkMonitor = mockk()
    private lateinit var viewModel: TodosViewModel
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
    fun `loadNextBatch Success state updates`() = runTest {
        // Verify that a successful repository response updates currentSkip by pageSize, sets isLoading to false, and clears all error states.
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returns NetworkResult.Success(
            dummyTodosResponse
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.networkError)
        viewModel.loadNextBatch()
        runCurrent()
        coVerify { todosRepository.refreshAllTodos(30, 30) }
    }

    @Test
    fun `loadNextBatch Re-entrancy protection`() = runTest {
        // Ensure that if isFetching is true, the function returns immediately without triggering a new repository call or updating UI state.
        coEvery { todosRepository.refreshAllTodos(any(), any()) } coAnswers {
            delay(1000)
            NetworkResult.Success(dummyTodosResponse)
        }
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent() // first call from init

        viewModel.loadNextBatch()
        runCurrent() // second call while the first is still fetching

        coVerify(exactly = 1) { todosRepository.refreshAllTodos(any(), any()) }
    }

    @Test
    fun `loadNextBatch Initial apiTotal assignment`() = runTest {
        // Confirm that apiTotal is correctly initialized from the repository result only when its current value is null.
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returnsMany listOf(
            NetworkResult.Success(dummyTodosResponse.copy(total = 100)),
            NetworkResult.Success(dummyTodosResponse.copy(total = 2))
        )

        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()
        viewModel.loadNextBatch()
        runCurrent() // loadNextBatched called twice, but the total should remain 100

        clearMocks(todosRepository, answers = false, recordedCalls = true)
        viewModel.onScrollReachedIndex(61) // if the total is still 100, loadNextBatch should be called again
        runCurrent()

        coVerify(exactly = 1) { todosRepository.refreshAllTodos(any(), any()) }
    }

    @Test
    fun `loadNextBatch Domain error handling`() = runTest {
        // Verify that NetworkResult.Error updates the UI state with the error message and sets isLoading to false while networkError remains null.
        val errorMessage = "Internal Server Error"
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returns NetworkResult.Error(
            errorMessage
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.networkError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadNextBatch Connectivity error handling`() = runTest {
        // Verify that NetworkResult.NetworkError updates the UI state with the network error message and sets isLoading to false while general error remains null.
        val errorMessage = "Network Error"
        coEvery {
            todosRepository.refreshAllTodos(
                any(), any()
            )
        } returns NetworkResult.NetworkError(errorMessage)
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        assertEquals(errorMessage, viewModel.uiState.value.networkError)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `toggleTodoStatus Successful toggle`() = runTest {
        // Verify that a successful toggle call to the repository does not emit any events to the error channel.
        coEvery { todosRepository.toggleStatus(any()) } returns NetworkResult.Success(
            dummyTodoListDto[0]
        )
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returns NetworkResult.Success(
            dummyTodosResponse
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        viewModel.errorEvent.test {
            viewModel.toggleTodoStatus(1)
            runCurrent()
            expectNoEvents()
        }
    }

    @Test
    fun `toggleTodoStatus Error channel emission on failure`() = runTest {
        // Check that NetworkResult.Error results in the specific error message being sent to the _errorChannel for UI consumption.
        val errorMessage = "Toggle Failed"
        coEvery { todosRepository.toggleStatus(any()) } returns NetworkResult.Error(
            errorMessage
        )
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returns NetworkResult.Success(
            dummyTodosResponse
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        viewModel.errorEvent.test {
            viewModel.toggleTodoStatus(1)
            assertEquals(errorMessage, awaitItem())
        }
    }

    @Test
    fun `toggleTodoStatus Network failure channel emission`() = runTest {
        // Check that NetworkResult.NetworkError results in the connectivity error message being sent to the _errorChannel.
        val errorMessage = "Network Error"
        coEvery { todosRepository.toggleStatus(any()) } returns NetworkResult.NetworkError(
            errorMessage
        )
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returns NetworkResult.Success(
            dummyTodosResponse
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        viewModel.errorEvent.test {
            viewModel.toggleTodoStatus(1)
            assertEquals(errorMessage, awaitItem())
        }
    }

    @Test
    fun `onScrollReachedIndex Threshold trigger logic`() = runTest {
        // Verify loadNextBatch is called when index is within the threshold (totalItems - 5) and all conditions (hasNext, !isFetching, !hasError) are met.
        val localEntity = listOf(dummyTodoListEntity[0])
        val remoteEntity = MutableStateFlow((List(10) { dummyTodoListEntity[1] }))
        val remoteDto = List(10) { dummyTodoListDto[1] }
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returnsMany listOf(
            NetworkResult.Success(
                dummyTodosResponse.copy(todos = remoteDto, skip = 0, limit = 10)
            ),
            NetworkResult.Success(
                dummyTodosResponse.copy(todos = remoteDto, skip = 10, limit = 10)
            )
        )
        every { todosRepository.observeRemoteTodos() } returns remoteEntity
        every { todosRepository.observeLocalTodos() } returns flowOf(localEntity)
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        // Total = 2H + 1L + 10R = 13 items
        // Threshold = 13 - 5 = 8
        viewModel.onScrollReachedIndex(7)
        runCurrent() // shouldn't trigger
        viewModel.onScrollReachedIndex(8)
        runCurrent() //threshold triggers
        // update uiState with new remote data
        val totalRemoteItems = List(20) { dummyTodoListEntity[1] }
        remoteEntity.value = totalRemoteItems
        runCurrent()

        // total calls = 2
        coVerify(exactly = 2) { todosRepository.refreshAllTodos(any(), any()) }

        assertEquals(totalRemoteItems.map { it.toDomain() }, viewModel.uiState.value.remoteData)
    }

    @Test
    fun `onScrollReachedIndex Missing apiTotal guard`() = runTest {
        // Ensure the function exits early if apiTotal is null, preventing pagination logic from executing before the first successful fetch.
        coEvery {
            todosRepository.refreshAllTodos(
                any(),
                any()
            )
        } returns NetworkResult.Error("Server Error")
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        viewModel.onScrollReachedIndex(0)
        runCurrent()
        viewModel.onScrollReachedIndex(90)
        runCurrent()
        coVerify(exactly = 1) { todosRepository.refreshAllTodos(any(), any()) }
    }

    @Test
    fun `onScrollReachedIndex Error state suppression`() = runTest {
        // Verify that scrolling does not trigger a new batch if either uiState.error or uiState.networkError is non-null.
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returnsMany listOf(
            NetworkResult.Success(dummyTodosResponse),
            NetworkResult.Error("Server Error"),
            NetworkResult.Success(dummyTodosResponse),
            NetworkResult.NetworkError("Network Error"),
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        viewModel.onScrollReachedIndex(0)
        runCurrent()
        assertNotNull(viewModel.uiState.value.error)

        // scrolling with error should not trigger
        viewModel.onScrollReachedIndex(0)
        runCurrent()

        // remove error
        viewModel.onRetry()
        runCurrent()
        assertNull(viewModel.uiState.value.error)

        // error removed so this scroll should trigger a refresh
        viewModel.onScrollReachedIndex(0)
        runCurrent()
        assertNotNull(viewModel.uiState.value.networkError)
        coVerify(exactly = 4) { todosRepository.refreshAllTodos(any(), any()) }
    }

    @Test
    fun `onScrollReachedIndex End of list guard`() = runTest {
        // Confirm loadNextBatch is not triggered if remoteItems is equal to or greater than apiTotal (hasNext is false).
        val remoteEntity = MutableStateFlow((List(10) { dummyTodoListEntity[1] }))
        val remoteDto = List(10) { dummyTodoListDto[1] }
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returnsMany listOf(
            NetworkResult.Success(
                dummyTodosResponse.copy(todos = remoteDto, total = 20, skip = 0, limit = 10)
            ),
            NetworkResult.Success(
                dummyTodosResponse.copy(todos = remoteDto, skip = 10, limit = 10)
            )
        )
        every { todosRepository.observeRemoteTodos() } returns remoteEntity
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns emptyFlow()

        // trigger 1
        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        // trigger 2
        viewModel.onScrollReachedIndex(11)
        runCurrent()
        remoteEntity.value = List(20) { dummyTodoListEntity[1] }
        runCurrent()

        // total has been reached, no more refreshing
        viewModel.onScrollReachedIndex(21)
        runCurrent()
        viewModel.onScrollReachedIndex(31)
        runCurrent()

        coVerify(exactly = 2) { todosRepository.refreshAllTodos(any(), any()) }
    }


    @Test
    fun `observeNetwork Auto retry on reconnection`() = runTest {
        // Ensure onRetry is automatically called when networkMonitor emits true and the UI state currently reflects a network error.
        val networkFlow = MutableStateFlow(false)
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returnsMany listOf(
            NetworkResult.NetworkError("Network Error"),
            NetworkResult.Success(dummyTodosResponse),
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns networkFlow

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        // reconnecting to the internet should trigger onRetry and refresh
        networkFlow.value = true
        runCurrent()

        coVerify(exactly = 2) { todosRepository.refreshAllTodos(any(), any()) }
    }

    @Test
    fun `observeNetwork Suppression of retry on stable connection`() = runTest {
        // Verify that network status changes to online do not trigger onRetry if there was no previous network error.
        val networkFlow = MutableStateFlow(false)
        coEvery { todosRepository.refreshAllTodos(any(), any()) } returns NetworkResult.Success(
            dummyTodosResponse
        )
        every { todosRepository.observeRemoteTodos() } returns emptyFlow()
        every { todosRepository.observeLocalTodos() } returns emptyFlow()
        every { networkMonitor.isOnline } returns networkFlow

        viewModel = TodosViewModel(todosRepository, networkMonitor)
        runCurrent()

        // reconnecting to the internet should not trigger onRetry because there is no Network Error
        networkFlow.value = true
        runCurrent()

        coVerify(exactly = 1) { todosRepository.refreshAllTodos(any(), any()) }
    }

}