package com.example.gitapp.ui.viewModelState.repositories

import com.example.gitapp.ui.data.models.Repo

sealed class RepositoriesListState {

    object Loading: RepositoriesListState()

    object Empty: RepositoriesListState()

    data class Success(
        val repos: List<Repo>
    ) : RepositoriesListState()

    data class Error(
        val message: String
    ) : RepositoriesListState()
}