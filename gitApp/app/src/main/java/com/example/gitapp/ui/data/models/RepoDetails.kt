package com.example.gitapp.ui.data.models

import com.google.gson.annotations.SerializedName

data class RepoDetails(
    val id: Long,
    val name: String,

    val language: String?,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("description")
    val description: String?,

    val owner: Owner,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("stargazers_count")
    val stargazersCount: Int,

    @SerializedName("watchers_count")
    val watchersCount: Int,

    @SerializedName("forks_count")
    val forksCount: Int,

    @SerializedName("license")
    val license: License?
)
