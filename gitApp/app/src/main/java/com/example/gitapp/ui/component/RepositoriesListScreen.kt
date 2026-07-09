package com.example.gitapp.ui.component

import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.handwriting.handwritingHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gitapp.ui.data.models.Repo
import com.example.gitapp.ui.viewModelState.repositories.RepositoriesListAction
import com.example.gitapp.ui.viewModelState.repositories.RepositoriesListState
import com.example.gitapp.ui.viewModelState.repositories.RepositoriesListViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.gitapp.R

@Composable
fun RepositoriesListScreen(
    navController: NavController,
    viewModel: RepositoriesListViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val actions = viewModel.action

    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is RepositoriesListAction.NavigateToDetails -> {
                    navController.navigate("details/${action.owner}/${action.repo}")
                }
                is RepositoriesListAction.NavigateToAuth -> {
                    navController.navigate("auth") {
                        popUpTo("repos") {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    when (state) {
        is RepositoriesListState.Loading -> {
            LoadingScreen()
        }

        is RepositoriesListState.Success -> {
            val repos = (state as RepositoriesListState.Success).repos

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.repositories_title),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.White
                    )

                    IconButton(
                        onClick = viewModel::onOutClick
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Выйти",
                            tint = Color.White
                        )
                    }
                }


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(repos) { repo ->
                        RepositoryCard(
                            repo = repo,
                            onClick = {
                                viewModel.onRepoClick(repo.owner.login, repo.name)
                            }
                        )
                    }
                }
            }
        }

        is RepositoriesListState.Error -> {
            ErrorScreen(
                message = (state as RepositoriesListState.Error).message,
                onRetry = { viewModel.refresh() }
            )
        }

        is RepositoriesListState.Empty -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No repositories found",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.refresh() }) {
                    Text("Refresh")
                }
            }
        }
    }
}

@Composable
fun RepositoryCard(
    repo: Repo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        onClick = onClick,
//        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize().background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                if (repo.language != null) {
                    Text(
                        text = repo.language,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        fontSize = 10.sp,
                        color = Color.Blue
                    )
                }
            }

            Text(
                text = "${repo.owner.login} • ⭐ ${repo.stargazersCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}