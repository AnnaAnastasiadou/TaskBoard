package com.example.taskboard.presentation.posts.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.core.SessionProvider
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.mapper.toDto
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val sessionProvider: SessionProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<PostUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val postId: Int = savedStateHandle.get<Int>("post_id")!!

    init {
        loadPostDetails()
    }

    private fun loadPostDetails() {
        viewModelScope.launch {
            if (postId != -1) {
                _uiState.update { it.copy(status = ScreenStatus.LOADING_DATA, data = null) }
                val postEntity = postsRepository.getPostById(postId)
                if (postEntity != null) {
                    _uiState.update {
                        it.copy(status = ScreenStatus.IDLE, data = postEntity.toDomain())
                    }
                } else {
                    _uiState.update {
                        it.copy(status = ScreenStatus.ERROR, data = null)
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        status = ScreenStatus.IDLE, data = Post(
                            id = 0,
                            userId = sessionProvider.getUserId() ?: 0,
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

    fun onRetry() {
        loadPostDetails()
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

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(data = it.data?.copy(title = newTitle)) }
    }

    fun updateBody(newBody: String) {
        _uiState.update { it.copy(data = it.data?.copy(body = newBody)) }
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
        if (currentTags.any { it.isBlank() }) return

        val updatedTags = currentTags.toMutableList()
        updatedTags.add("")

        _uiState.update { it.copy(data = it.data?.copy(tags = updatedTags)) }
    }

    suspend fun savePost() {
        val currentPost = _uiState.value.data ?: return

        val invalidIndices = currentPost.tags.mapIndexedNotNull { index, tag ->
            if (tag.isBlank()) index else null
        }.toSet()

        val errors = PostValidationError(
            titleError = if (currentPost.title.isBlank()) "Title is required" else null,
            bodyError = if (currentPost.body.isBlank()) "Title is required" else null,
            tagsError = if (invalidIndices.isNotEmpty()) "Tags cannot be blank" else if (currentPost.tags.isEmpty()) "Please insert at least 1 tag" else null,
            errorTagIndices = invalidIndices
        )

        _uiState.update { it.copy(validationError = errors) }
        if (errors.titleError != null || errors.bodyError != null || errors.tagsError != null) {
            return
        }

        val requestBody = mapOf(
            "title" to currentPost.title, "body" to currentPost.body, "tags" to currentPost.tags
        )

        val isEditMode = postId != -1

        val result = if (isEditMode) postsRepository.updatePost(
            postId,
            body = requestBody
        ) else postsRepository.addPost(currentPost.toDto())

        _uiState.update { it.copy(status = ScreenStatus.SAVING, snackBarMessage = "Saving...") }
        when (result) {
            is NetworkResult.Error -> _uiState.update {
                it.copy(
                    status = ScreenStatus.IDLE,
                    snackBarMessage = result.message
                )
            }

            is NetworkResult.NetworkError -> _uiState.update {
                it.copy(
                    status = ScreenStatus.IDLE,
                    snackBarMessage = result.message
                )
            }

            is NetworkResult.Success -> {
                _uiState.update {
                    it.copy(
                        status = ScreenStatus.IDLE,
                        snackBarMessage = null
                    )
                }
                _uiEvent.send(PostUiEvent.NavigateBack)
            }
        }
    }

    suspend fun deletePost() {
        val post = uiState.value.data ?: return
        _uiState.update { it.copy(status = ScreenStatus.DELETING, snackBarMessage = "Deleting...") }
        when (val result = postsRepository.deletePost(post)) {
            is NetworkResult.Success -> {
                _uiState.update {
                    it.copy(
                        snackBarMessage = null
                    )
                }
                _uiEvent.send(PostUiEvent.NavigateBack)
            }

            is NetworkResult.NetworkError -> _uiState.update {
                it.copy(
                    status = ScreenStatus.IDLE,
                    snackBarMessage = result.message
                )
            }

            is NetworkResult.Error -> _uiState.update {
                it.copy(
                    status = ScreenStatus.IDLE,
                    snackBarMessage = result.message
                )
            }
        }
    }
    fun clearSnackBarMessage() {
        _uiState.update { it.copy(snackBarMessage = null) }
    }
}