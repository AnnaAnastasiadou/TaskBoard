package com.example.taskboard.presentation.home

import androidx.lifecycle.ViewModel
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.domain.repository.ProfileRepository
import com.example.taskboard.domain.repository.TodosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val postsRepository: PostsRepository,
    private val todosRepository: TodosRepository
) : ViewModel() {
}