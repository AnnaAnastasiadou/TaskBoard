package com.example.taskboard.presentation.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.presentation.auth.LoginActivity
import com.example.taskboard.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect
                    val destination =
                        if (state.isLoggedIn) MainActivity::class.java else LoginActivity::class.java
                    /**
                     * An 'Intent' is a messaging object you use to request an action from
                     * another app component (like an Activity). Here, we are telling
                     * Android: "I am 'this' (MainActivity) and I want to open 'LoginActivity'."
                     * This will pause MainActivity and start the LoginActivity lifecycle (onCreate, onStart, etc.).
                     */
                    startActivity(Intent(this@LauncherActivity, destination))
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