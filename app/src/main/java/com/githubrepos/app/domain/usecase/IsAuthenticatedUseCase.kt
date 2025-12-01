package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.repository.GitHubRepository

class IsAuthenticatedUseCase(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isAuthenticated()
    }
}

