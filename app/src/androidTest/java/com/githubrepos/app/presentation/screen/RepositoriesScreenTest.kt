package com.githubrepos.app.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.githubrepos.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoriesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signInScreen_displaysSignInButton() {
        composeTestRule.setContent {
            SignInScreen(
                onSignInSuccess = {}
            )
        }

        composeTestRule.onNodeWithText("Sign In with GitHub")
            .assertIsDisplayed()
    }

    @Test
    fun signInScreen_displaysAppTitle() {
        composeTestRule.setContent {
            SignInScreen(
                onSignInSuccess = {}
            )
        }

        composeTestRule.onNodeWithText("GitHub Repos")
            .assertIsDisplayed()
    }
}

