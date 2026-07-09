package com.example.gitapp.ui.viewModelState.details

sealed class ReadmeState {

    object Loading : ReadmeState()

    object Empty : ReadmeState()

    data class Success(
        val readme: String
    ) : ReadmeState()

    data class Error(
        val message: String
    ) : ReadmeState()
}