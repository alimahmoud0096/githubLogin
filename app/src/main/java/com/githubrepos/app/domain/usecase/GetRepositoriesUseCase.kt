package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.repository.GitHubRepository

class GetRepositoriesUseCase(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(
        page: Int,
        perPage: Int = 30
    ): Result<List<Repository>> {
        return repository.getAuthenticatedUserRepositories(page, perPage)
    }
}

