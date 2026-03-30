package com.example.taskboard.presentation.posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.model.Todo
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import com.example.taskboard.presentation.common.pagination.BasePaginationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList


@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    networkMonitor: NetworkMonitor
) : BasePaginationViewModel<Post>(networkMonitor) {

    override val dataFlow = postsRepository.getAllPosts().map { entities ->
        entities?.map { it.toDomain() }
            ?: emptyList()
    }

    override fun loadNextBatch() {
        if (isFetching) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
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

}

