package com.githubrepos.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githubrepos.app.domain.model.Branch
import com.githubrepos.app.domain.usecase.GetBranchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BranchesUiState(
    val branches: List<Branch> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class BranchesViewModel @Inject constructor(
    private val getBranchesUseCase: GetBranchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BranchesUiState())
    val uiState: StateFlow<BranchesUiState> = _uiState.asStateFlow()

    private var owner: String = ""
    private var repo: String = ""

    fun loadBranches(owner: String, repo: String, refresh: Boolean = false) {
        this.owner = owner
        this.repo = repo

        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(
                    currentPage = 1,
                    branches = emptyList(),
                    hasMore = true
                )
            }

            val page = if (refresh) 1 else _uiState.value.currentPage
            _uiState.value = _uiState.value.copy(
                isLoading = page == 1,
                isLoadingMore = page > 1,
                error = null
            )

            getBranchesUseCase(owner, repo, page, 30).fold(
                onSuccess = { branches ->
                    val currentBranches = if (refresh) emptyList() else _uiState.value.branches
                    _uiState.value = _uiState.value.copy(
                        branches = currentBranches + branches,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = page + 1,
                        hasMore = branches.size == 30
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Failed to load branches"
                    )
                }
            )
        }
    }

    fun loadMoreBranches() {
        if (_uiState.value.hasMore && !_uiState.value.isLoadingMore) {
            loadBranches(owner, repo, refresh = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

