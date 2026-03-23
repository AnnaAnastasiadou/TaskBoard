package com.example.taskboard.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskboard.R
import com.example.taskboard.databinding.HomeFragmentBinding
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.model.Todo

class HomeFragment: Fragment(R.layout.home_fragment) {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding : HomeFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = HomeFragmentBinding.bind(view)

        val postAdapter = RecentPostsAdapter(getDummyPosts(), {})
        binding.rvRecentPosts.adapter = postAdapter

        val todosAdapter = PendingTodosAdapter(getDummyTodos(), {})
        binding.rvPendingTodos.adapter = todosAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun getDummyPosts(): List<Post> {
    return listOf(
        Post(
            id = 1,
            userId = 101,
            title = "Mastering ConstraintLayout",
            body = "Learning how to anchor views to the top and bottom bars is key to a solid UI...",
            tags = listOf("Android", "UI"),
            updatedAt = "2026-03-20".toLong(),
            likes = 1,
            dislikes = 4
        ),
        Post(
            id = 2,
            userId = 101,
            title = "The Power of RecyclerView",
            body = "Adapters and ViewHolders allow us to display long lists efficiently by recycling views...",
            tags = listOf("Kotlin", "Performance"),
            updatedAt = "2026-03-19".toLong(),
            likes = 45,
            dislikes = 1
        )
    )
}

private fun getDummyTodos(): List<Todo> {
    return listOf(
        Todo(
            id = 1,
            userId = 1,
            todo = "Check the API documentation",
            completed = false,
            updatedAt = "2026-03-20"
        ),
        Todo(
            id = 2,
            userId = 1,
            todo = "Buy groceries for dinner",
            completed = false,
            updatedAt = "2026-03-20"
        ),
        Todo(
            id = 3,
            userId = 1,
            todo = "Call the bank regarding the loan",
            completed = true, // This one should be filtered out!
            updatedAt = "2026-03-19"
        ),
        Todo(
            id = 4,
            userId = 1,
            todo = "Send the weekly report to the manager",
            completed = false,
            updatedAt = "2026-03-20"
        ),
        Todo(
            id = 5,
            userId = 1,
            todo = "Clean the workspace",
            completed = false,
            updatedAt = "2026-03-18"
        )
    )
}