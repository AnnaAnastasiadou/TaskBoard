package com.example.taskboard.presentation.todos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.PostsFragmentBinding
import com.example.taskboard.databinding.TodosFragmentBinding

class TodosFragment: Fragment(R.layout.todos_fragment) {
    private var _binding : TodosFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = TodosFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}