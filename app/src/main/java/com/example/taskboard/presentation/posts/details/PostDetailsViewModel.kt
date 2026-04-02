package com.example.taskboard.presentation.posts.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val postId: Int = savedStateHandle.get<Int>("post_id")!!

    init {
        loadPostDetails()
    }


    private fun loadPostDetails() {
        viewModelScope.launch {
            if (postId != -1) {
                _uiState.update { it.copy(isLoading = true, data = null) }
                val postEntity = postsRepository.getPostById(postId)
                if (postEntity != null) {
                    _uiState.update {
                        it.copy(isLoading = false, data = postEntity.toDomain())
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        data = Post(
                            id = 0,
                            userId = profileRepository.getUserId(),
                            title = "",
                            body = "",
                            tags = emptyList(),
                            likes = 0,
                            dislikes = 0,
                        )
                    )
                }
            }
        }
    }

    fun removeTagAt(index: Int) {
        val currentTags = _uiState.value.data?.tags?.toMutableList() ?: return
        if (index in currentTags.indices) {
            currentTags.removeAt(index)
        }
        _uiState.update {
            it.copy(data = it.data?.copy(tags = currentTags))
        }
    }

    fun updateTagAt(index: Int, newText: String) {
        val currentTags = _uiState.value.data?.tags?.toMutableList() ?: return

        if (newText.isBlank() && index < currentTags.size - 1) {
            currentTags.removeAt(index)
        }

        if (index in currentTags.indices && currentTags[index] != newText) {
            currentTags[index] = newText
            _uiState.update {
                it.copy(data = it.data?.copy(tags = currentTags))
            }
        }

    }

    fun addEmptyTag() {
        val currentPost = _uiState.value.data ?: return
        val currentTags = currentPost.tags
        if (currentTags.isNotEmpty() && currentTags.last().isBlank()) {
            return
        }

        val updatedTags = currentTags.toMutableList()
        updatedTags.add("")

        _uiState.update { it.copy(data = it.data?.copy(tags = updatedTags)) }
    }
}