package com.example.taskboard.presentation.posts.list

import android.content.Intent
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
import com.example.taskboard.databinding.PostListFragmentBinding
import com.example.taskboard.domain.model.Post
import com.example.taskboard.presentation.common.ListLoadState
import com.example.taskboard.presentation.common.ListLoadStateAdapter
import com.example.taskboard.presentation.posts.details.PostDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.post_list_fragment) {
    private val viewModel: PostsViewModel by viewModels()
    private var _binding: PostListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var listLoadStateAdapter: ListLoadStateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = PostListFragmentBinding.bind(view)

        observeUiState()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        postsAdapter = PostsAdapter(emptyList()) { clickedPostId ->
            val intent = Intent(requireContext(), PostDetailsActivity::class.java).apply {
                putExtra("post_id", clickedPostId)
            }
            startActivity(intent)
        }

        listLoadStateAdapter = ListLoadStateAdapter { viewModel.onRetry() }

        binding.rvPosts.adapter = ConcatAdapter(postsAdapter, listLoadStateAdapter)

        binding.btnAddPost.setOnClickListener {
            val intent = Intent(requireContext(), PostDetailsActivity::class.java).apply {
                putExtra("post_id", -1)
            }
            startActivity(intent)
        }

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

                    val displayList = mutableListOf<Any>()
                    if (!state.localData.isNullOrEmpty()) {
                        displayList.add("Local Posts")
                        displayList.addAll(state.localData)
                    }
                    if (!state.remoteData.isNullOrEmpty()) {
                        displayList.add("Remote Posts")
                        displayList.addAll(state.remoteData)
                    }
                    postsAdapter.updateData(displayList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

