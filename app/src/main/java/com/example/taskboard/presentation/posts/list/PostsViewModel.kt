package com.example.taskboard.presentation.posts.list

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import com.example.taskboard.presentation.common.pagination.BasePaginationViewModel
import com.example.taskboard.presentation.common.pagination.BaseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val networkMonitor: NetworkMonitor
): ViewModel() {

    val localDataFlow = postsRepository.observeLocalPosts()
        .map { entities ->
        entities.map { it.toDomain() }
    }
    val remoteDataFlow = postsRepository.observeRemotePosts()
        .map { entities ->
        entities.map { it.toDomain() }
    }

    private val _uiState = MutableStateFlow(PostsUiState())
    val uiState = _uiState.asStateFlow()

    private var currentSkip = 0
    private var pageSize = 30
    private var isFetching = false

    init {
        observeLocalData()
        observeRemoteData()
        observeNetwork()
        loadNextBatch()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            localDataFlow.collect { list ->
                _uiState.update { it.copy(localData = list) }
            }
        }
    }

    private fun observeRemoteData() {
        viewModelScope.launch {
            remoteDataFlow.collect { list ->
                _uiState.update { it.copy(remoteData = list) }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val hasNetworkError = _uiState.value.networkError != null
                if (online && hasNetworkError) {
                    onRetry()
                }
            }
        }
    }

    fun loadNextBatch() {
        if (isFetching) return
        viewModelScope.launch {
            isFetching = true
            _uiState.update { it.copy(isLoading = true) }

            val result = postsRepository.refreshPosts(pageSize, currentSkip)
            when (result) {
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

        val localList = uiState.value.localData ?: emptyList()
        val remoteList = uiState.value.remoteData ?: emptyList()
        val localHeader = if (localList.isNotEmpty()) 1 else 0
        val remoteHeader = if (remoteList.isNotEmpty()) 1 else 0
        val totalItems = localList.size + remoteList.size + localHeader + remoteHeader

        if (index >= totalItems - 5 && !isFetching && !hasError) {
            loadNextBatch()
        }
    }

    fun onRetry() {
        loadNextBatch()
    }

}
