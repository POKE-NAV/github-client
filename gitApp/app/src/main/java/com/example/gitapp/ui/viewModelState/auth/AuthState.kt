package com.example.gitapp.ui.viewModelState.auth

import com.example.gitapp.ui.data.models.UserInfo

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userInfo: UserInfo) : AuthState()
    data class Error(val message: String) : AuthState()
}