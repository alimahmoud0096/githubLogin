package com.githubrepos.app.domain.usecase

import com.githubrepos.app.domain.model.Branch
import com.githubrepos.app.domain.repository.GitHubRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetBranchesUseCaseTest {

    @Mock
    private lateinit var repository: GitHubRepository

    private lateinit var useCase: GetBranchesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetBranchesUseCase(repository)
    }

    @Test
    fun `invoke returns success with branches`() = runTest {
        // Given
        val expectedBranches = listOf(
            Branch(name = "main", sha = "abc123", isProtected = true),
            Branch(name = "develop", sha = "def456", isProtected = false)
        )
        whenever(repository.getRepositoryBranches("owner", "repo", 1, 30))
            .thenReturn(Result.success(expectedBranches))

        // When
        val result = useCase("owner", "repo", 1, 30)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedBranches, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        whenever(repository.getRepositoryBranches("owner", "repo", 1, 30))
            .thenReturn(Result.failure(exception))

        // When
        val result = useCase("owner", "repo", 1, 30)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}

