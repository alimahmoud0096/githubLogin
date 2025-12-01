package com.githubrepos.app.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.githubrepos.app.presentation.navigation.NavGraph
import com.githubrepos.app.presentation.ui.theme.GitHubReposTheme
import com.githubrepos.app.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            GitHubReposTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthCallback(intent)
    }

    override fun onResume() {
        super.onResume()
        // Check for OAuth callback when activity resumes
        handleOAuthCallback(intent)
    }

    private fun handleOAuthCallback(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "githubrepos" && data.host == "callback") {
            val code = data.getQueryParameter("code")
            val error = data.getQueryParameter("error")
            
            if (error != null) {
                // OAuth was cancelled or failed
                OAuthCallbackHandler.error = error
                OAuthCallbackHandler.errorDescription = data.getQueryParameter("error_description")
            } else if (code != null) {
                OAuthCallbackHandler.code = code
            }
        }
    }
}

// Shared state for OAuth callback
object OAuthCallbackHandler {
    private var _code: String? = null
    var code: String?
        get() = _code
        set(value) {
            _code = value
            if (value != null) {
                callbackReceived = true
            }
        }
    
    var error: String? = null
    var errorDescription: String? = null
    @Volatile
    var callbackReceived: Boolean = false
    
    fun clear() {
        _code = null
        error = null
        errorDescription = null
        callbackReceived = false
    }
}

@Composable
fun MainContent() {
    val authViewModel: AuthViewModel = hiltViewModel()
    var processedCode by remember { mutableStateOf<String?>(null) }
    var checkCounter by remember { mutableStateOf(0) }

    // Check for OAuth code - triggered by checkCounter
    LaunchedEffect(checkCounter) {
        // Check for OAuth code
        OAuthCallbackHandler.code?.let { code ->
            if (code != processedCode) {
                processedCode = code
                authViewModel.authenticate(code)
                OAuthCallbackHandler.clear()
            }
        }
        
        // Check for OAuth error (user cancelled, etc.)
        OAuthCallbackHandler.error?.let { error ->
            // Error is already logged, just clear it
            OAuthCallbackHandler.clear()
        }
    }

    // Periodically check for OAuth callback
    // This helps catch callbacks that happen while the activity is in the background
    LaunchedEffect(Unit) {
        // Initial check
        checkCounter++
        
        // Then check periodically when callback is received
        while (true) {
            delay(300) // Check every 300ms
            if (OAuthCallbackHandler.callbackReceived) {
                checkCounter++
                // Reset the flag after checking
                OAuthCallbackHandler.callbackReceived = false
            }
        }
    }

    NavGraph()
}

