package com.example.taskboard.presentation.posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var currentSkip = 0
    private var pageSize = 30
    private var isFetching = false

    init {
        subscribeToPosts()
        observeNetworkRecovery()
        loadNextBatch()
    }

    private fun observeNetworkRecovery() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val hasNetworkError = _uiState.value.networkError != null
                if (online && hasNetworkError) {
                    loadNextBatch()
                }
            }
        }
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
        println("calling load batch")
        if (isFetching) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, networkError = null) }
            isFetching = true

            when (val result = postsRepository.refreshPosts(30, currentSkip)) {
                is NetworkResult.Success -> {
                    currentSkip += pageSize
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            networkError = null
                        )
                    }
                }

                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message,
                        networkError = null
                    )
                }

                is NetworkResult.NetworkError -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        networkError = result.message
                    )
                }
            }
            isFetching = false

        }
    }

    fun onScrollReachedIndex(index: Int) {
        val hasError = uiState.value.error != null || uiState.value.networkError != null
        val totalItems = uiState.value.data?.size ?: 0
        if (index >= totalItems - 5 && !isFetching && !hasError) {
            loadNextBatch()
        }
    }

    fun onRetry() {
        loadNextBatch()
    }
}