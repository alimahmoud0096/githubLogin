package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.model.Owner
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.repository.GitHubRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SearchRepositoriesUseCaseTest {

    @Mock
    private lateinit var repository: GitHubRepository

    private lateinit var useCase: SearchRepositoriesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SearchRepositoriesUseCase(repository)
    }

    @Test
    fun `invoke with blank query calls getAuthenticatedUserRepositories`() = runTest {
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
        val result = useCase("", 1, 30)

        // Then
        verify(repository).getAuthenticatedUserRepositories(1, 30)
        assertTrue(result.isSuccess)
        assertEquals(expectedRepositories, result.getOrNull())
    }

    @Test
    fun `invoke with query calls searchRepositories`() = runTest {
        // Given
        val query = "test"
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
        whenever(repository.searchRepositories(query, 1, 30))
            .thenReturn(Result.success(expectedRepositories))

        // When
        val result = useCase(query, 1, 30)

        // Then
        verify(repository).searchRepositories(query, 1, 30)
        assertTrue(result.isSuccess)
        assertEquals(expectedRepositories, result.getOrNull())
    }
}

