package com.example.taskboard.presentation.todos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.example.taskboard.databinding.TodosFragmentBinding
import com.example.taskboard.presentation.common.pagination.ListLoadState
import com.example.taskboard.presentation.common.pagination.ListLoadStateAdapter
import com.example.taskboard.presentation.common.showErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodosFragment : Fragment(R.layout.todos_fragment) {
    private val viewModel: TodosViewModel by viewModels()
    private var _binding: TodosFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var todosAdapter: TodosAdapter
    private lateinit var listLoadStateAdapter: ListLoadStateAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = TodosFragmentBinding.bind(view)
        setUpRecyclerView()
        observeUiState()
    }

    private fun setUpRecyclerView() {
        todosAdapter = TodosAdapter(emptyList(), {}, { id -> viewModel.toggleTodoStatus(id) })
        listLoadStateAdapter = ListLoadStateAdapter { viewModel.onRetry() }
        binding.rvTodos.adapter = ConcatAdapter(todosAdapter, listLoadStateAdapter)

        binding.rvTodos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

                        state.networkError != null -> listLoadStateAdapter.setState(
                            ListLoadState.NetworkError(
                                state.networkError
                            )
                        )

                        else -> listLoadStateAdapter.setState(ListLoadState.Hidden)
                    }

                    todosAdapter.updateData(state.data ?: emptyList())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorEvent.collect { message ->
                    showErrorMessage(binding.root, message)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}