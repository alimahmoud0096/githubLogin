package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.model.Owner
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetRepositoriesUseCaseTest {

    @Mock
    private lateinit var repository: GitHubRepository

    private lateinit var useCase: GetRepositoriesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetRepositoriesUseCase(repository)
    }

    @Test
    fun `invoke returns success with repositories`() = runTest {
        // Given
        val expectedRepositories = listOf(
            Repository(
                id = 1,
                name = "repo1",
                fullName = "owner/repo1",
                description = "Description",
                isPrivate = false,
                starsCount = 10,
                language = "Kotlin",
                updatedAt = "2024-01-01",
                owner = Owner(login = "owner", avatarUrl = "https://avatar.com")
            )
        )
        whenever(repository.getAuthenticatedUserRepositories(1, 30))
            .thenReturn(Result.success(expectedRepositories))

        // When
        val result = useCase(1, 30)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedRepositories, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        whenever(repository.getAuthenticatedUserRepositories(1, 30))
            .thenReturn(Result.failure(exception))

        // When
        val result = useCase(1, 30)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}

