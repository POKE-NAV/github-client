package com.example.gitapp.ui.data.api
import com.example.gitapp.ui.data.models.ReadmeResponse
import com.example.gitapp.ui.data.models.Repo
import com.example.gitapp.ui.data.models.RepoDetails
import com.example.gitapp.ui.data.models.UserInfo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("user")
    suspend fun getUserInfo(): UserInfo

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "update",
        @Query("direction") direction: String = "desc"
    ) : List<Repo>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails (
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepoDetails

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ReadmeResponse
}