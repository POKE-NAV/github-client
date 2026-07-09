package com.example.gitapp.ui.viewModelState.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitapp.ui.data.repository.AppRepository
import com.example.gitapp.ui.data.repository.RepositoryResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepositoryInfoViewModel (
    val repository: AppRepository,
    val owner : String,
    val repoName : String
) : ViewModel() {

    private val _state = MutableStateFlow<RepositoryInfoState>(RepositoryInfoState.Loading)
    val state : StateFlow<RepositoryInfoState> = _state.asStateFlow()

    private val _action = MutableSharedFlow<RepositoryInfoAction>()
    val action : SharedFlow<RepositoryInfoAction> = _action.asSharedFlow()

    init {
        loadRepositoryInfo()
    }

    fun loadRepositoryInfo() {
        viewModelScope.launch {
            _state.value = RepositoryInfoState.Loading

            val detailsResult = repository.getRepositoryDetails(owner, repoName)
            val readmeResult = repository.getRepositoryReadme(owner, repoName)

            when(detailsResult) {
                is RepositoryResult.Success -> {
                    val details = detailsResult.data

                    val readmeState =  when(readmeResult) {
                        is RepositoryResult.Success -> {
                            ReadmeState.Success(readmeResult.data)
                        }
                        is RepositoryResult.Error -> {
                            ReadmeState.Error(readmeResult.message)
                        }
                        is RepositoryResult.Loading -> {
                            ReadmeState.Loading
                        }
                        is RepositoryResult.Empty -> {
                            ReadmeState.Empty
                        }
                    }
                    _state.value = RepositoryInfoState.Success(
                        details,
                        readmeState
                    )
                }
                is RepositoryResult.Error -> {
                    _state.value = RepositoryInfoState.Error(detailsResult.message)
                }
                is RepositoryResult.Loading -> {}
                is RepositoryResult.Empty -> {}
            }
        }
    }

    fun openInBrowser(url: String) {
        viewModelScope.launch {
            _action.emit(RepositoryInfoAction.OpenInBrowser(url))
        }
    }

    fun onGoBackClick() {
        viewModelScope.launch {
            _action.emit(RepositoryInfoAction.GoBack)
        }
    }

    fun refresh() {
        loadRepositoryInfo()
    }
}