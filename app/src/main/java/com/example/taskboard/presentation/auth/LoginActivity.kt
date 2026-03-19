package com.example.taskboard.presentation.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.databinding.LoginActivityBinding
import com.example.taskboard.presentation.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            viewModel.logIn(binding.etUsername.text.toString(), binding.etPassword.text.toString())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.btnLogin.visibility = if (!state.isLoading) View.VISIBLE else View.GONE
                    binding.loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    state.error?.let { errorMessage ->
                        val snackBar = Snackbar.make(binding.loginRoot, errorMessage, Snackbar.LENGTH_SHORT)
                        snackBar.setBackgroundTint(Color.RED)
                        snackBar.setTextColor(Color.WHITE)
                        snackBar.show()
                        viewModel.clearError()
                    }
                    if (state.isLoading) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}