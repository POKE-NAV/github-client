package com.example.gitapp.ui.data.repository

sealed class RepositoryResult<out T> {

    data class Success<T>(val data: T) : RepositoryResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null
    ) : RepositoryResult<Nothing>()

    object Loading: RepositoryResult<Nothing>()

    object Empty: RepositoryResult<Nothing>()
}