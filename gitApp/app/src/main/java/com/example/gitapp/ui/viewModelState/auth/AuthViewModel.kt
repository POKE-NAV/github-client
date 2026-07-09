package com.example.gitapp.ui.viewModelState.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitapp.ui.data.repository.AppRepository
import com.example.gitapp.ui.data.repository.RepositoryResult
import com.example.gitapp.ui.data.storage.KeyValueStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    val repository: AppRepository,
    val storage: KeyValueStorage
) : ViewModel() {

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _actions = MutableSharedFlow<AuthAction>()
    val actions: SharedFlow<AuthAction> = _actions.asSharedFlow()

    fun updateToken(newToken:  String) {
        _token.value = newToken.trim()
        if (_state.value is AuthState.Error) {
            _state.value = AuthState.Idle
        }
    }

    fun onSingInClick() {
        viewModelScope.launch {
            val tokenValue = _token.value

            if(tokenValue.isEmpty()) {
                _state.value = AuthState.Error("Token cannot be empty")
                return@launch
            }

            _state.value = AuthState.Loading

            val result = repository.signIn(tokenValue)

            when (result) {
                is RepositoryResult.Success -> {
                    _state.value = AuthState.Success(result.data)
                    _actions.emit(AuthAction.NavigateToMain)
                }
                is RepositoryResult.Error -> {
                    _state.value = AuthState.Error(result.message)

                }
                is RepositoryResult.Loading -> { }

                is RepositoryResult.Empty -> { }
            }
        }
    }

    fun checkAuthState() {
        viewModelScope.launch {
            val isAuthorized = repository.checkAuthState()
            if(isAuthorized) {
                _actions.emit(AuthAction.NavigateToMain)
            }
        }
    }

    fun clearState() {
        _state.value = AuthState.Idle
        _token.value = ""
    }
}