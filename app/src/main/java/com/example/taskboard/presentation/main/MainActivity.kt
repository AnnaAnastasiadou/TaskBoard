package com.example.taskboard.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.taskboard.R
import com.example.taskboard.databinding.ActivityMainBinding
import com.example.taskboard.presentation.home.HomeFragment
import com.example.taskboard.presentation.posts.PostsFragment
import com.example.taskboard.presentation.profile.ProfileFragment
import com.example.taskboard.presentation.todos.TodosFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//    sealed class for possible navigation
sealed class Screen(val tag: String) {
    object Home : Screen("home_fragment")
    object Posts : Screen("posts_fragment")
    object Todos : Screen("todos_fragment")
    object Profile : Screen("profile_fragment")
}
    private lateinit var binding: ActivityMainBinding
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> { true }
                R.id.navigation_posts -> {true}
                R.id.navigation_todos -> {true}
                R.id.navigation_profile -> {true}
                else -> false
            }
        }

        if(savedInstanceState == null) {
            switchFragment(Screen.Home)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }
    }

    fun switchFragment(screen: Screen) {
        val fragmentManager = supportFragmentManager
        val targetFragment: Fragment = fragmentManager.findFragmentByTag(screen.tag) ?: when (screen) {
            Screen.Home -> HomeFragment()
            Screen.Posts -> PostsFragment()
            Screen.Todos -> TodosFragment()
            Screen.Profile -> ProfileFragment()
        }

        val transaction = fragmentManager.beginTransaction()
        activeFragment?.let {transaction.hide(it)}

        if(!targetFragment.isAdded) {
            transaction.add(R.id.fragment_container, targetFragment, screen.tag)
        } else {
            transaction.show(targetFragment)
        }
        transaction.commit()
        activeFragment = targetFragment
    }
}