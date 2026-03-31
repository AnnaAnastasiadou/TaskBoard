package com.example.taskboard.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.R
import com.example.taskboard.databinding.ActivityMainBinding
import com.example.taskboard.domain.repository.AuthRepository
import com.example.taskboard.presentation.auth.LoginActivity
import com.example.taskboard.presentation.home.HomeFragment
import com.example.taskboard.presentation.posts.PostsFragment
import com.example.taskboard.presentation.profile.ProfileFragment
import com.example.taskboard.presentation.todos.TodosFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //    sealed class for possible navigation
    sealed class Screen(val tag: String, val title: String) {
        object Posts : Screen("posts_fragment", "Posts")
        object Todos : Screen("todos_fragment", "Todos")
        object Profile : Screen("profile_fragment", "Profile")
    }

    private lateinit var binding: ActivityMainBinding
    private var activeFragment: Fragment? = null
    @Inject lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.topBar)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authRepository.isLoggedIn.collect { loggedIn ->
                    if(!loggedIn) {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_posts -> {
                    switchFragment(Screen.Posts)
                    true
                }

                R.id.navigation_todos -> {
                    switchFragment(Screen.Todos)
                    true
                }

                R.id.navigation_profile -> {
                    switchFragment(Screen.Profile)
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState == null) {
            switchFragment(Screen.Posts)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }
    }

    fun switchFragment(screen: Screen) {
        val fragmentManager = supportFragmentManager
        val targetFragment: Fragment =
            fragmentManager.findFragmentByTag(screen.tag) ?: when (screen) {
                Screen.Posts -> PostsFragment()
                Screen.Todos -> TodosFragment()
                Screen.Profile -> ProfileFragment()
            }

        supportActionBar?.title = screen.title

        val transaction = fragmentManager.beginTransaction()
        activeFragment?.let { transaction.hide(it) }

        if (!targetFragment.isAdded) {
            transaction.add(R.id.fragment_container, targetFragment, screen.tag)
        } else {
            transaction.show(targetFragment)
        }
        transaction.commit()
        activeFragment = targetFragment
    }
}