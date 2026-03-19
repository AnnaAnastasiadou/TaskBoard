package com.example.taskboard.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.ProfileFragmentBinding
import com.example.taskboard.databinding.TodosFragmentBinding

class ProfileFragment: Fragment(R.layout.profile_fragment) {
    private var _binding : ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ProfileFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}