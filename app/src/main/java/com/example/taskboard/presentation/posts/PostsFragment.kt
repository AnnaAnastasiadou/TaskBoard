package com.example.taskboard.presentation.posts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.PostsFragmentBinding
import com.example.taskboard.domain.model.Post

class PostsFragment : Fragment(R.layout.posts_fragment) {
    private var _binding: PostsFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = PostsFragmentBinding.bind(view)
        val adapter = PostsAdapter(dummyList, {})
        binding.rvPosts.adapter = adapter
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
    ),
    Post(
        id = 2,
        title = "The Magic of Material3 Chips",
        body = "Chips are compact elements that represent an attribute or action. They look great in a ChipGroup.",
        tags = listOf("UI", "Design"),
        likes = 45,
        dislikes = 0,
        userId = 2,
        updatedAt = null,
    ),
    Post(
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
