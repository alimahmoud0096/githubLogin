package com.githubrepos.app.domain.repository

import com.githubrepos.app.domain.model.Branch
import com.githubrepos.app.domain.model.Repository

interface GitHubRepository {
    suspend fun getAuthenticatedUserRepositories(
        page: Int,
        perPage: Int = 30
    ): Result<List<Repository>>

    suspend fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int = 30
    ): Result<List<Repository>>

    suspend fun getRepositoryBranches(
        owner: String,
        repo: String,
        page: Int,
        perPage: Int = 30
    ): Result<List<Branch>>

    suspend fun authenticateWithCode(code: String): Result<String>
    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(token: String)
    suspend fun clearAccessToken()
    suspend fun isAuthenticated(): Boolean
}

