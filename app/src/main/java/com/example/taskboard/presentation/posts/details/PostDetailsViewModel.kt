package com.example.taskboard.presentation.posts.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    val _uiState = MutableStateFlow(PostDetailsUiState())
    private val uiState = _uiState.asStateFlow()

    private val postId: Int? = savedStateHandle.get<Int>("post_id")

    init {
        loadPostDetails()
    }


    private fun loadPostDetails() {
        viewModelScope.launch {
            if (postId != null) {
                _uiState.update { it.copy(isLoading = true, data = null) }
                val postEntity = postsRepository.getPostById(postId)
                if (postEntity != null) {
                    _uiState.update {
                        it.copy(isLoading = false, data = postEntity.toDomain())
                    }
                }
            }
        }
    }
}