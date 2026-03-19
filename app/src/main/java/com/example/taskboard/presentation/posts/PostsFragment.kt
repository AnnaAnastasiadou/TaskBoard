package com.example.taskboard.presentation.posts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.PostsFragmentBinding

class PostsFragment : Fragment(R.layout.posts_fragment) {
    private var _binding : PostsFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = PostsFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}