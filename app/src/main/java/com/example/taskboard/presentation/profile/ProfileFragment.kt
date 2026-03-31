package com.example.taskboard.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.transform.CircleCropTransformation
import com.example.taskboard.R
import com.example.taskboard.databinding.ProfileFragmentBinding
import com.example.taskboard.databinding.TodosFragmentBinding
import com.example.taskboard.presentation.common.showErrorMessage
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ProfileFragmentBinding.bind(view)

        binding.stateLayout.btnRetry.setOnClickListener {
            viewModel.onRetry()
        }

        observeUi()
    }

    private fun observeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.combine(viewModel.userProfile){ state, user ->

                        if (user != null) {
                            binding.profileGroup.isVisible = true
                            binding.stateLayout.root.isVisible = false

                            binding.ivProfile.load(user.image) {
                                crossfade(true)
                                placeholder(R.drawable.ic_profile)
                                error(R.drawable.ic_profile)
                                transformations(CircleCropTransformation())
                            }
                            binding.tvFullName.text = "${user.firstName} ${user.lastName}"
                            binding.tvUsername.text = "@${user.username}"
                            binding.tvEmailValue.text = user.email
                            binding.tvPhoneValue.text = user.phone

                            (state.error ?: state.networkError)?.let { msg ->
                                showErrorMessage(binding.root, msg)
                            }

                        } else {
                            binding.stateLayout.root.isVisible = true
                            binding.profileGroup.isVisible = false

                            val hasError = state.error != null || state.networkError != null
                            val isLoading = state.isLoading

                            binding.stateLayout.progressBar.isVisible = isLoading
                            binding.stateLayout.tvError.isVisible = hasError
                            binding.stateLayout.tvError.text = state.error ?: state.networkError
                            binding.stateLayout.btnRetry.isVisible = state.error != null
                        }
                    }.collect()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}