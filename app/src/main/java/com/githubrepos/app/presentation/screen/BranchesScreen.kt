package com.githubrepos.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.githubrepos.app.R
import com.githubrepos.app.domain.model.Branch
import com.githubrepos.app.domain.model.Repository
import com.githubrepos.app.presentation.viewmodel.BranchesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchesScreen(
    repository: Repository,
    onNavigateBack: () -> Unit,
    viewModel: BranchesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(repository) {
        viewModel.loadBranches(repository.owner.login, repository.name, refresh = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repository.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.branches.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.branches.isEmpty() -> {
                ErrorState(
                    message = uiState.error!!,
                    onRetry = {
                        viewModel.loadBranches(repository.owner.login, repository.name, refresh = true)
                    },
                    onDismiss = { viewModel.clearError() },
//                    modifier = Modifier.padding(padding)
                )
            }

            uiState.branches.isEmpty() -> {
                EmptyState(
                    message = stringResource(R.string.no_branches),
//                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.branches),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(uiState.branches) { branch ->
                        BranchItem(branch = branch)
                    }

                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (uiState.hasMore && !uiState.isLoadingMore) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.loadMoreBranches()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BranchItem(branch: Branch) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = branch.sha.take(7),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (branch.isProtected) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.protected_txt)) }
                )
            }
        }
    }
}

