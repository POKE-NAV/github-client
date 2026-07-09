package com.example.gitapp.ui.data.models

import com.google.gson.annotations.SerializedName

data class Repo(
    val id: String,
    val name: String,
    val language: String?,
    val description: String?,
    val owner: Owner,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("stargazers_count")
    val stargazersCount: Int
)
