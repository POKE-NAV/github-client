package com.example.gitapp.ui.network

import com.example.gitapp.ui.data.repository.AppRepository
import com.example.gitapp.ui.data.storage.KeyValueStorage
import com.example.gitapp.ui.viewModelState.auth.AuthViewModel
import com.example.gitapp.ui.viewModelState.details.RepositoryInfoViewModel
import com.example.gitapp.ui.viewModelState.repositories.RepositoriesListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    single { KeyValueStorage(androidContext()) }
}

val networkModule = module {
    single { AuthInterceptor(get()) }
    single { NetworkModule.provideOkHttpsClient(get()) }
    single { NetworkModule.provideRetrofit(get()) }
    single { NetworkModule.provideGitHubApi(get()) }
}

val repositoryModule = module {
    single { AppRepository(get(), get()) }
}

val viewModelModule = module {
    factory { AuthViewModel(get(), get()) }
    factory { RepositoriesListViewModel(get(), get()) }
    factory { (owner: String, repo: String) ->
        RepositoryInfoViewModel(get(), owner, repo)
    }
}

val appModule = storageModule + networkModule + repositoryModule + viewModelModule