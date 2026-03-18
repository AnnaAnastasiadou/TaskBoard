package com.example.taskboard.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.presentation.main.RootViewModel
import com.example.taskboard.databinding.ActivityMainBinding
import com.example.taskboard.presentation.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//    sealed class for possible navigation

    private val viewModel: RootViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state.isLoggedIn) {
                        true -> {}
                        false -> {
                            /**
                             * Define the Destination
                             * An 'Intent' is a messaging object you use to request an action from
                             * another app component (like an Activity). Here, we are telling
                             * Android: "I am 'this' (MainActivity) and I want to open 'LoginActivity'."
                             */
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            /**
                             * Launch the New Screen
                             * This sends the request to the Android System. The OS will then
                             * pause MainActivity and start the LoginActivity lifecycle (onCreate, onStart, etc.).
                             */
                            startActivity(intent)

                            /**
                             * Clean up the "Back Stack"
                             * In Android, screens are stacked like a deck of cards. By default,
                             * if you don't call finish(), MainActivity stays at the bottom of the deck.
                             * * We call finish() here because if a user is NOT logged in, we want
                             * them to "exit" the app if they press the back button, rather than
                             * returning to an empty, white MainActivity screen.
                             * * Calling this effectively "destroys" this instance of MainActivity.
                             */
                            finish()
                        }
                    }
                }
            }
        }
    }
}