package com.example.gitapp.ui.viewModelState.details

import com.example.gitapp.ui.data.models.ReadmeResponse
import com.example.gitapp.ui.data.models.RepoDetails

sealed class RepositoryInfoState {

    object Loading : RepositoryInfoState()

    data class Success(
        val repoDetails: RepoDetails,
        val readmeState: ReadmeState
    ) : RepositoryInfoState()

    data class Error(
        val message: String
    ) : RepositoryInfoState()
}