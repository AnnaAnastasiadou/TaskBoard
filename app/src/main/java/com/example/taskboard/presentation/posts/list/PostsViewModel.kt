package com.example.taskboard.presentation.posts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.common.NetworkMonitor
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
    private var totalItems: Int? = null

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

            when (val result = postsRepository.refreshPosts(pageSize, currentSkip)) {
                is NetworkResult.Success -> {
                    currentSkip += pageSize
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            networkError = null
                        )
                    }
                    if (totalItems == null) {
                        totalItems = result.data.total
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
        if (totalItems == null) return
        val hasError = uiState.value.error != null || uiState.value.networkError != null

        val localItems = uiState.value.localData?.size ?: 0
        val remoteItems = uiState.value.remoteData?.size ?: 0
        val hasNext = totalItems!! > (localItems + remoteItems)
        val localHeader = if (localItems != 0) 1 else 0
        val remoteHeader = if (remoteItems != 0) 1 else 0
        val totalItems = localItems + remoteItems + localHeader + remoteHeader

        if (index >= totalItems - 5 && !isFetching && !hasError && hasNext) {
            loadNextBatch()
        }
    }

    fun onRetry() {
        loadNextBatch()
    }

}
