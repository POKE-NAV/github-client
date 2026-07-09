package com.example.gitapp.ui.viewModelState.repositories

import android.os.Message

sealed class RepositoriesListAction {
    data class NavigateToDetails(val owner: String, val repo: String) : RepositoriesListAction()
    object NavigateToAuth: RepositoriesListAction()
//    data class ShowError(val message: String) : RepositoriesListAction()
}