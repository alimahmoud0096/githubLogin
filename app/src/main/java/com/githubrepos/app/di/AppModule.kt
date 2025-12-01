package com.githubrepos.app.di

import com.githubrepos.app.data.local.TokenStorage
import com.githubrepos.app.data.remote.NetworkModule
import com.githubrepos.app.data.remote.api.GitHubApi
import com.githubrepos.app.data.repository.GitHubRepositoryImpl
import com.githubrepos.app.domain.repository.GitHubRepository
import com.githubrepos.app.domain.usecase.AuthenticateUseCase
import com.githubrepos.app.domain.usecase.GetBranchesUseCase
import com.githubrepos.app.domain.usecase.GetRepositoriesUseCase
import com.githubrepos.app.domain.usecase.IsAuthenticatedUseCase
import com.githubrepos.app.domain.usecase.SearchRepositoriesUseCase
import com.githubrepos.app.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi() = NetworkModule.provideMoshi()

    @Provides
    @Singleton
    fun provideOkHttpClient() = NetworkModule.provideOkHttpClient()

    @Provides
    @Singleton
    @Named("api")
    fun provideGitHubApi(
        okHttpClient: okhttp3.OkHttpClient,
        moshi: com.squareup.moshi.Moshi
    ): GitHubApi = NetworkModule.provideGitHubApi(okHttpClient, moshi)

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthApi(
        okHttpClient: okhttp3.OkHttpClient,
        moshi: com.squareup.moshi.Moshi
    ): GitHubApi = NetworkModule.provideAuthApi(okHttpClient, moshi)

    @Provides
    @Singleton
    fun provideGitHubRepository(
        @Named("api") api: GitHubApi,
        @Named("auth") authApi: GitHubApi,
        tokenStorage: TokenStorage
    ): GitHubRepository {
        return GitHubRepositoryImpl(api, authApi, tokenStorage)
    }

    @Provides
    fun provideGetRepositoriesUseCase(repository: GitHubRepository) =
        GetRepositoriesUseCase(repository)

    @Provides
    fun provideSearchRepositoriesUseCase(repository: GitHubRepository) =
        SearchRepositoriesUseCase(repository)

    @Provides
    fun provideGetBranchesUseCase(repository: GitHubRepository) =
        GetBranchesUseCase(repository)

    @Provides
    fun provideAuthenticateUseCase(repository: GitHubRepository) =
        AuthenticateUseCase(repository)

    @Provides
    fun provideSignOutUseCase(repository: GitHubRepository) =
        SignOutUseCase(repository)

    @Provides
    fun provideIsAuthenticatedUseCase(repository: GitHubRepository) =
        IsAuthenticatedUseCase(repository)
}

