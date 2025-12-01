package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.model.Branch
import com.githubrepos.app.domain.repository.GitHubRepository

class GetBranchesUseCase(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        page: Int,
        perPage: Int = 30
    ): Result<List<Branch>> {
        return repository.getRepositoryBranches(owner, repo, page, perPage)
    }
}

