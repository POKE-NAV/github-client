package com.example.gitapp.ui.data.repository

import android.util.Base64
import com.example.gitapp.ui.data.api.GitHubApi
import com.example.gitapp.ui.data.models.Owner
import com.example.gitapp.ui.data.models.Repo
import com.example.gitapp.ui.data.models.RepoDetails
import com.example.gitapp.ui.data.models.UserInfo
import com.example.gitapp.ui.data.storage.KeyValueStorage
import retrofit2.HttpException
import java.io.IOException

class AppRepository(
    private val api: GitHubApi,
    private val storage: KeyValueStorage
) {
    companion object {
        private const val DEFAULT_PER_PAGE = 10
    }

    suspend fun signIn(token: String): RepositoryResult<UserInfo> {
        return try {
            storage.authToken = token
            val userInfo = api.getUserInfo()
            RepositoryResult.Success(userInfo)
        } catch (e: HttpException) {
            storage.authToken = null
            when (e.code()) {
                401 -> RepositoryResult.Error("Invalid token. Please check and try again.")
                403 -> RepositoryResult.Error("Access forbidden. Token may have insufficient permissions.")
                else -> RepositoryResult.Error("Server error: ${e.message()}")
            }
        } catch (e: IOException) {
            storage.authToken = null
            RepositoryResult.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            storage.authToken = null
            RepositoryResult.Error("Unknown error: ${e.message}")
        }
    }

    suspend fun getRepositories(): RepositoryResult<List<Repo>> {
        return try {
            val repos = api.getUserRepositories(
                DEFAULT_PER_PAGE,
                1,
                "update",
                "desc"
            )
             if (repos.isEmpty()) {
                 RepositoryResult.Empty
             } else {
                 RepositoryResult.Success(repos)
             }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> RepositoryResult.Error("Invalid token. Please check and try again.")
                403 -> RepositoryResult.Error("Access forbidden. Token may have insufficient permissions.")
                else -> RepositoryResult.Error("Server error: ${e.message()}")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            RepositoryResult.Error("Unknown error: ${e.message}")
        }
    }

    suspend fun  getRepositoryDetails(
        owner: String,
        repo : String
    ) : RepositoryResult<RepoDetails> {
        return try {
            val details = api.getRepositoryDetails(owner, repo)
            RepositoryResult.Success(details)
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> RepositoryResult.Error("Repository not found.")
                401 -> RepositoryResult.Error("Authentication failed. Please sign in again.")
                403 -> RepositoryResult.Error("Access forbidden.")
                else -> RepositoryResult.Error("Failed to load details: ${e.message()}")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            RepositoryResult.Error("Unknown error: ${e.message}")
        }
    }

    suspend fun getRepositoryReadme(
        owner: String,
        repo: String
    ): RepositoryResult<String> {
        return try {
            val response = api.getReadme(owner, repo)

            val decodedContent = try {
                val decoded = Base64.decode(response.content, Base64.DEFAULT)
                String(decoded, Charsets.UTF_8)
            } catch (e: Exception) {
                try {
                    val decoded = Base64.decode(response.content, Base64.DEFAULT)
                    String(decoded, Charsets.ISO_8859_1)
                } catch (e2: Exception) {
                    ""
                }
            }

            if (decodedContent.isEmpty()) {
                RepositoryResult.Empty
            } else {
                RepositoryResult.Success(decodedContent)
            }
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> RepositoryResult.Empty
                401 -> RepositoryResult.Error("Authentication failed. Please sign in again.")
                403 -> RepositoryResult.Error("Access forbidden.")
                else -> RepositoryResult.Error("Failed to load README: ${e.message()}")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            RepositoryResult.Error("Unknown error: ${e.message}")
        }
    }

    suspend fun checkAuthState(): Boolean {
        val token = storage.authToken
        return if(!token.isNullOrEmpty()) {
            try{
                api.getUserInfo()
                true
            } catch (e: Exception) {
                storage.authToken = null
                false
            }
        } else {
            false
        }
    }

    suspend fun signOut() {
        storage.authToken = null
    }
}