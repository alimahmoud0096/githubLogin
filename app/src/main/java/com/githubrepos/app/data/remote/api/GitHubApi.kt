package com.githubrepos.app.data.remote.api

import com.githubrepos.app.data.remote.dto.AuthResponseDto
import com.githubrepos.app.data.remote.dto.BranchDto
import com.githubrepos.app.data.remote.dto.RepositoryDto
import com.githubrepos.app.data.remote.dto.SearchResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("user/repos")
    suspend fun getAuthenticatedUserRepositories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("sort") sort: String = "updated"
    ): Response<List<RepositoryDto>>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<SearchResponseDto>

    @GET("repos/{owner}/{repo}/branches")
    suspend fun getRepositoryBranches(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<BranchDto>>

    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Response<String>
}

