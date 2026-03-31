package com.example.taskboard.presentation.posts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.databinding.PostsFragmentBinding
import com.example.taskboard.domain.model.Post
import com.example.taskboard.presentation.common.pagination.ListLoadState
import com.example.taskboard.presentation.common.pagination.ListLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.posts_fragment) {
    private val viewModel: PostsViewModel by viewModels()
    private var _binding: PostsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var postsAdapter: PostsAdapter
    private lateinit var listLoadStateAdapter: ListLoadStateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = PostsFragmentBinding.bind(view)

        observeUiState()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        postsAdapter = PostsAdapter(emptyList(), {})

        listLoadStateAdapter = ListLoadStateAdapter { viewModel.onRetry() }

        binding.rvPosts.adapter = ConcatAdapter(postsAdapter, listLoadStateAdapter)

        binding.rvPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                viewModel.onScrollReachedIndex(lastVisiblePosition)
            }
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when {
                        state.isLoading -> listLoadStateAdapter.setState(ListLoadState.Loading)
                        state.error != null -> listLoadStateAdapter.setState(
                            ListLoadState.Error(
                                state.error
                            )
                        )

                        state.networkError != null -> {
                            listLoadStateAdapter.setState(ListLoadState.NetworkError(state.networkError))
                        }

                        else -> {
                            listLoadStateAdapter.setState(ListLoadState.Hidden)
                        }
                    }
                    postsAdapter.updateData(newPostsList = state.data ?: emptyList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

val dummyList = listOf(
    Post(
        id = 1,
        title = "Learning Android Data Binding",
        body = "Data binding is powerful but can be tricky when IDs conflict. Always check your XML names!",
        tags = listOf("Android", "Kotlin", "Development"),
        likes = 124,
        dislikes = 2,
        userId = 1,
        updatedAt = System.currentTimeMillis(),
    ), Post(
        id = 2,
        title = "The Magic of Material3 Chips",
        body = "Chips are compact elements that represent an attribute or action. They look great in a ChipGroup.",
        tags = listOf("UI", "Design"),
        likes = 45,
        dislikes = 0,
        userId = 2,
        updatedAt = null,
    ), Post(
        id = 3,
        title = "Quick Tips for Clean Code",
        body = "Keep your fragments lean and your adapters smart. Logic belongs in the ViewModel!",
        tags = listOf("CleanCode", "Architecture", "Solid", "BestPractices", "Mobile"),
        likes = 89,
        dislikes = 1,
        userId = 3,
        updatedAt = System.currentTimeMillis(),
    )
)
