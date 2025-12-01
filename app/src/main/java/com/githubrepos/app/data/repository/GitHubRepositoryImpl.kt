package com.githubrepos.app.data.repository

import com.githubrepos.app.BuildConfig
import com.githubrepos.app.data.local.TokenStorage
import com.githubrepos.app.data.mapper.toDomain
import com.githubrepos.app.data.remote.api.GitHubApi
import com.githubrepos.app.domain.model.Branch
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val api: GitHubApi,
    private val authApi: GitHubApi,
    private val tokenStorage: TokenStorage
) : GitHubRepository {

    override suspend fun getAuthenticatedUserRepositories(
        page: Int,
        perPage: Int
    ): Result<List<Repository>> = withContext(Dispatchers.IO) {
        try {
            val token = tokenStorage.getToken() ?: return@withContext Result.failure(
                Exception("Not authenticated")
            )

            val response = api.getAuthenticatedUserRepositories(
                token = "token $token",
                page = page,
                perPage = perPage
            )

            if (response.isSuccessful && response.body() != null) {
                val repositories = response.body()!!.map { it.toDomain() }
                Result.success(repositories)
            } else {
                Result.failure(
                    Exception("Failed to fetch repositories: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int
    ): Result<List<Repository>> = withContext(Dispatchers.IO) {
        try {
            val token = tokenStorage.getToken() ?: return@withContext Result.failure(
                Exception("Not authenticated")
            )

            // Search only user's repositories
            val searchQuery = "user:@me $query"
            val response = api.searchRepositories(
                token = "token $token",
                query = searchQuery,
                page = page,
                perPage = perPage
            )

            if (response.isSuccessful && response.body() != null) {
                val repositories = response.body()!!.items.map { it.toDomain() }
                Result.success(repositories)
            } else {
                Result.failure(
                    Exception("Failed to search repositories: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRepositoryBranches(
        owner: String,
        repo: String,
        page: Int,
        perPage: Int
    ): Result<List<Branch>> = withContext(Dispatchers.IO) {
        try {
            val token = tokenStorage.getToken() ?: return@withContext Result.failure(
                Exception("Not authenticated")
            )

            val response = api.getRepositoryBranches(
                token = "token $token",
                owner = owner,
                repo = repo,
                page = page,
                perPage = perPage
            )

            if (response.isSuccessful && response.body() != null) {
                val branches = response.body()!!.map { it.toDomain() }
                Result.success(branches)
            } else {
                Result.failure(
                    Exception("Failed to fetch branches: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun authenticateWithCode(code: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = authApi.exchangeCodeForToken(
                    clientId = BuildConfig.GITHUB_CLIENT_ID,
                    clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
                    code = code
                )

                if (response.isSuccessful && response.body() != null) {
                    // Parse form-encoded response: access_token=xxx&token_type=bearer&scope=repo
                    val responseBody = response.body()!!
                    val params = responseBody.split("&").associate { param ->
                        val parts = param.split("=", limit = 2)
                        parts[0] to (if (parts.size > 1) parts[1] else "")
                    }
                    val accessToken = params["access_token"]
                    if (accessToken != null) {
                        tokenStorage.saveToken(accessToken)
                        Result.success(accessToken)
                    } else {
                        Result.failure(Exception("Access token not found in response"))
                    }
                } else {
                    Result.failure(
                        Exception("Failed to authenticate: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAccessToken(): String? {
        return tokenStorage.getToken()
    }

    override suspend fun saveAccessToken(token: String) {
        tokenStorage.saveToken(token)
    }

    override suspend fun clearAccessToken() {
        tokenStorage.clearToken()
    }

    override suspend fun isAuthenticated(): Boolean {
        return tokenStorage.hasToken()
    }
}

