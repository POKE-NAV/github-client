package com.example.gitapp.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gitapp.R
import com.example.gitapp.ui.data.models.RepoDetails
import com.example.gitapp.ui.viewModelState.details.ReadmeState
import com.example.gitapp.ui.viewModelState.details.RepositoryInfoAction
import com.example.gitapp.ui.viewModelState.details.RepositoryInfoState
import com.example.gitapp.ui.viewModelState.details.RepositoryInfoViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailInfoScreen(
    navController: NavController,
    owner: String,
    repo: String,
    viewModel: RepositoryInfoViewModel = koinViewModel(
        parameters = { parametersOf(owner, repo) }
    )
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val actions = viewModel.action

    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is RepositoryInfoAction.OpenInBrowser -> {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(action.url)
                    )
                    context.startActivity(intent)
                }
                is RepositoryInfoAction.GoBack -> {
                    navController.navigate("repos") {
                        popUpTo("details/{owner}/{repo}") {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    when (state) {
        is RepositoryInfoState.Loading -> {
            LoadingScreen()
        }

        is RepositoryInfoState.Error -> {
            ErrorScreen(
                message = (state as RepositoryInfoState.Error).message,
                onRetry = { viewModel.refresh() }
            )
        }

        is RepositoryInfoState.Success -> {
            val data = state as RepositoryInfoState.Success
            DetailContent(
                viewModel = viewModel,
                repoDetails = data.repoDetails,
                readmeState = data.readmeState,
                onOpenBrowser = { viewModel.openInBrowser(it) }
            )
        }
    }
}

@Composable
fun DetailContent(
    viewModel: RepositoryInfoViewModel,
    repoDetails: RepoDetails,
    readmeState: ReadmeState,
    onOpenBrowser: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Название
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = repoDetails.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                IconButton(
                    onClick = viewModel::onGoBackClick
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Выйти",
                        tint = Color.White
                    )
                }
            }

        }

        // 2. Владелец
        item {
            Text(
                text = "Влделец: ${repoDetails.owner.login}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        // 3. Ссылка на GitHub
        item {
            Button(
                onClick = { onOpenBrowser(repoDetails.htmlUrl) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(Color.Green,Color.White, Color.Green,Color.White)
            ) {
                Text(stringResource(R.string.view_on_github))
            }
        }

        // 4. Лицензия
        item {
            if (repoDetails.license != null) {
                Text(
                    text = "License: ${repoDetails.license.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        // 5. Статистика
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = stringResource(R.string.stars),
                    value = repoDetails.stargazersCount.toString(),
                )
                StatItem(
                    label = stringResource(R.string.forks),
                    value = repoDetails.forksCount.toString()
                )
                StatItem(
                    label = stringResource(R.string.watchers),
                    value = repoDetails.watchersCount.toString()
                )
            }
        }

        // 6. README
        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = stringResource(R.string.readme),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            when (readmeState) {
                is ReadmeState.Loading -> {
                    LoadingScreen()
                }

                is ReadmeState.Success -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = readmeState.readme,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                is ReadmeState.Empty -> {
                    Text(
                        text = stringResource(R.string.no_readme),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is ReadmeState.Error -> {
                    Text(
                        text = "Error: ${readmeState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}