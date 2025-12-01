package com.githubrepos.app.presentation.viewmodel

import app.cash.turbine.test
import com.githubrepos.app.domain.usecase.AuthenticateUseCase
import com.githubrepos.app.domain.usecase.IsAuthenticatedUseCase
import com.githubrepos.app.domain.usecase.SignOutUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @Mock
    private lateinit var authenticateUseCase: AuthenticateUseCase

    @Mock
    private lateinit var isAuthenticatedUseCase: IsAuthenticatedUseCase

    @Mock
    private lateinit var signOutUseCase: SignOutUseCase

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AuthViewModel(authenticateUseCase, isAuthenticatedUseCase, signOutUseCase)
    }

    @Test
    fun `checkAuthenticationStatus sets authenticated to true when user is authenticated`() = runTest {
        // Given
        whenever(isAuthenticatedUseCase.invoke()).thenReturn(true)

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            viewModel.checkAuthenticationStatus()
            
            // Then
            val state = awaitItem()
            assertTrue(state.isLoading)
            
            val authenticatedState = awaitItem()
            assertFalse(authenticatedState.isLoading)
            assertTrue(authenticatedState.isAuthenticated)
        }
    }

    @Test
    fun `authenticate sets loading and updates state on success`() = runTest {
        // Given
        val code = "auth_code"
        val token = "access_token"
        whenever(authenticateUseCase.invoke(code)).thenReturn(Result.success(token))

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            viewModel.authenticate(code)
            
            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(null, loadingState.error)
            
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isAuthenticated)
        }
    }

    @Test
    fun `authenticate sets error on failure`() = runTest {
        // Given
        val code = "auth_code"
        val error = Exception("Authentication failed")
        whenever(authenticateUseCase.invoke(code)).thenReturn(Result.failure(error))

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            viewModel.authenticate(code)
            
            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.isAuthenticated)
            assertEquals("Authentication failed", errorState.error)
        }
    }

    @Test
    fun `signOut clears authentication`() = runTest {
        // When
        viewModel.signOut()

        // Then
        verify(signOutUseCase).invoke()
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
        }
    }
}

