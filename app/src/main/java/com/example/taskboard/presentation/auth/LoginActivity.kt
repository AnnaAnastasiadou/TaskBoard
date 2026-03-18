package com.example.taskboard.presentation.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskboard.databinding.LoginActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}