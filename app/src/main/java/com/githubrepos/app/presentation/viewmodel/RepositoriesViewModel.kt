package com.githubrepos.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.usecase.GetRepositoriesUseCase
import com.githubrepos.app.domain.usecase.SearchRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepositoriesUiState(
    val repositories: List<Repository> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoriesUiState())
    val uiState: StateFlow<RepositoriesUiState> = _uiState.asStateFlow()

    init {
        loadRepositories()
    }

    fun loadRepositories(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(
                    currentPage = 1,
                    repositories = emptyList(),
                    hasMore = true
                )
            }

            val page = if (refresh) 1 else _uiState.value.currentPage
            _uiState.value = _uiState.value.copy(
                isLoading = page == 1,
                isLoadingMore = page > 1,
                error = null
            )

            val query = _uiState.value.searchQuery
            val result = if (query.isBlank()) {
                getRepositoriesUseCase(page, 30)
            } else {
                searchRepositoriesUseCase(query, page, 30)
            }

            result.fold(
                onSuccess = { repositories ->
                    val currentRepos = if (refresh) emptyList() else _uiState.value.repositories
                    _uiState.value = _uiState.value.copy(
                        repositories = currentRepos + repositories,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = page + 1,
                        hasMore = repositories.size == 30
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Failed to load repositories"
                    )
                }
            )
        }
    }

    fun searchRepositories(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                currentPage = 1,
                repositories = emptyList(),
                hasMore = true
            )
            loadRepositories(refresh = true)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

