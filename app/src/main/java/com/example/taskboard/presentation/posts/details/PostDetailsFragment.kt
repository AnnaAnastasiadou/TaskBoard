package com.example.taskboard.presentation.posts.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.PostDetailsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment : Fragment(R.layout.post_details_fragment) {

    private var _binding: PostDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = PostDetailsFragmentBinding.bind(view)
    }
}