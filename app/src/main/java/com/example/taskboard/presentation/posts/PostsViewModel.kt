package com.example.taskboard.presentation.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    val _uiState = MutableStateFlow(PostsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var currentSkip = 0
    private var pageSize = 30
    private var isFetching = false

    init {
        subscribeToPosts()
        loadNextBatch()
    }

    private fun subscribeToPosts() {
        viewModelScope.launch {
            postsRepository.getAllPosts().collect { entities ->
                val domainList = entities?.map { it.toDomain() } ?: emptyList()
                _uiState.update { it.copy(isLoading = false, error = null, data = domainList) }
            }
        }
    }

    private fun loadNextBatch() {
        if (isFetching) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            isFetching = true

            when (val result = postsRepository.refreshPosts(30, currentSkip)) {
                is NetworkResult.Success -> {
                    delay(5000)
                    currentSkip += pageSize
                    isFetching = false
                    _uiState.update { it.copy(isLoading = false)}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun onScrollReachedIndex(index: Int) {
        val totalItems = uiState.value.data?.size ?: 0
        if (index >= totalItems - 1 && !isFetching) {
            loadNextBatch()
        }
    }
}