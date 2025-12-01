package com.githubrepos.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.presentation.screen.BranchesScreen
import com.githubrepos.app.presentation.screen.RepositoriesScreen
import com.githubrepos.app.presentation.screen.SignInScreen
import com.githubrepos.app.presentation.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object Repositories : Screen("repositories")
    data class Branches(val repository: Repository? = null) : Screen("branches/{owner}/{repo}") {
        companion object {
            const val route = "branches/{owner}/{repo}"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()

    // Navigate to repositories when authentication succeeds
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Screen.Repositories.route) {
                popUpTo(Screen.SignIn.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isAuthenticated) Screen.Repositories.route else Screen.SignIn.route
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Repositories.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Repositories.route) {
            RepositoriesScreen(
                onRepositoryClick = { repository ->
                    navController.navigate("branches/${repository.owner.login}/${repository.name}")
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Repositories.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Branches.route) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: return@composable
            val repo = backStackEntry.arguments?.getString("repo") ?: return@composable

            // Create a minimal repository object for navigation
            val repository = Repository(
                id = 0,
                name = repo,
                fullName = "$owner/$repo",
                description = null,
                isPrivate = false,
                starsCount = 0,
                language = null,
                updatedAt = "",
                owner = com.githubrepos.app.domain.model.Owner(login = owner, avatarUrl = "")
            )

            BranchesScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

