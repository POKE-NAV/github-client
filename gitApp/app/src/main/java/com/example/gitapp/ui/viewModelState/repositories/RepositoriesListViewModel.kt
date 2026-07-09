package com.example.gitapp.ui.viewModelState.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitapp.ui.data.repository.AppRepository
import com.example.gitapp.ui.data.repository.RepositoryResult
import com.example.gitapp.ui.data.storage.KeyValueStorage
import com.example.gitapp.ui.viewModelState.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepositoriesListViewModel(
    private val repositories: AppRepository,
    val storage: KeyValueStorage
): ViewModel() {
    private val _state = MutableStateFlow<RepositoriesListState>(RepositoriesListState.Loading)
    val state : StateFlow<RepositoriesListState> = _state.asStateFlow()

    private val _action = MutableSharedFlow<RepositoriesListAction>()
    val action : SharedFlow<RepositoriesListAction> = _action.asSharedFlow()

    init {
        loadRepositories()
    }
    fun loadRepositories() {
        viewModelScope.launch {
            _state.value = RepositoriesListState.Loading
            val result = repositories.getRepositories()

            when(result) {
                is RepositoryResult.Success -> {
                    _state.value = RepositoriesListState.Success(result.data)
                }
                is RepositoryResult.Error -> {
                    _state.value = RepositoriesListState.Error(result.message)
                }

                is RepositoryResult.Empty -> { _state.value = RepositoriesListState.Empty }
                is RepositoryResult.Loading -> { }
            }
        }
    }

    fun onRepoClick(
        owner: String,
        repo: String
    ) {
        viewModelScope.launch {
            _action.emit(RepositoriesListAction.NavigateToDetails(owner, repo))
        }
    }

    fun onOutClick() {
        viewModelScope.launch {
            storage.clearAll()
            _action.emit(RepositoriesListAction.NavigateToAuth)
        }
    }

    fun refresh()
    {
        loadRepositories()
    }
}