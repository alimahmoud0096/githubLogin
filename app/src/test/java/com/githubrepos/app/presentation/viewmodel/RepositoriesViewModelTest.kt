package com.githubrepos.app.presentation.viewmodel

import app.cash.turbine.test
import com.githubrepos.app.domain.model.Owner
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.domain.usecase.GetRepositoriesUseCase
import com.githubrepos.app.domain.usecase.SearchRepositoriesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoriesViewModelTest {

    @Mock
    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase

    @Mock
    private lateinit var searchRepositoriesUseCase: SearchRepositoriesUseCase

    private lateinit var viewModel: RepositoriesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `initial state has empty repositories`() = runTest {
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, searchRepositoriesUseCase)
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(emptyList<Repository>(), initialState.repositories)
            assertFalse(initialState.isLoading)
        }
    }

    @Test
    fun `loadRepositories sets loading state and updates repositories on success`() = runTest {
        // Given
        val repositories = listOf(
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
        whenever(getRepositoriesUseCase.invoke(1, 30)).thenReturn(Result.success(repositories))
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, searchRepositoriesUseCase)

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            viewModel.loadRepositories()
            
            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(repositories, successState.repositories)
            assertEquals(2, successState.currentPage)
        }
    }

    @Test
    fun `loadRepositories sets error state on failure`() = runTest {
        // Given
        val error = Exception("Network error")
        whenever(getRepositoriesUseCase.invoke(1, 30)).thenReturn(Result.failure(error))
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, searchRepositoriesUseCase)

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            viewModel.loadRepositories()
            
            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Network error", errorState.error)
        }
    }

    @Test
    fun `searchRepositories updates search query and loads results`() = runTest {
        // Given
        val query = "test"
        val repositories = listOf(
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
        whenever(searchRepositoriesUseCase.invoke(query, 1, 30)).thenReturn(Result.success(repositories))
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, searchRepositoriesUseCase)

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            viewModel.searchRepositories(query)
            
            // Then
            val state = awaitItem()
            assertEquals(query, state.searchQuery)
            assertEquals(emptyList<Repository>(), state.repositories) // Cleared first
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
        }
    }
}

