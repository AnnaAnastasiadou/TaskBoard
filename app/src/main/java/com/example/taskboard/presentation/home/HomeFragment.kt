package com.example.taskboard.presentation.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.HomeFragmentBinding
import com.example.taskboard.databinding.TodosFragmentBinding

class HomeFragment: Fragment(R.layout.home_fragment) {
    private var _binding : HomeFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = HomeFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}