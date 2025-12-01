package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.repository.GitHubRepository

class AuthenticateUseCase(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(code: String): Result<String> {
        return repository.authenticateWithCode(code)
    }
}

