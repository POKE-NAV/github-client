package com.example.gitapp.ui.viewModelState.details

sealed class RepositoryInfoAction {

    data class OpenInBrowser(val url: String) : RepositoryInfoAction()

//    data class ShowError(val message : String) : RepositoryInfoAction()

    object GoBack : RepositoryInfoAction()
}