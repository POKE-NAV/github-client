package com.example.gitapp.ui.viewModelState.auth

sealed class AuthAction {
    object NavigateToMain : AuthAction()
//    data class ShowToast(val message: String) : AuthAction()
}