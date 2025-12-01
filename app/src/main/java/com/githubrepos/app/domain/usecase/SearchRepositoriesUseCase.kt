package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.repository.GitHubRepository

class SearchRepositoriesUseCase(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        perPage: Int = 30
    ): Result<List<Repository>> {
        if (query.isBlank()) {
            return repository.getAuthenticatedUserRepositories(page, perPage)
        }
        return repository.searchRepositories(query, page, perPage)
    }
}

