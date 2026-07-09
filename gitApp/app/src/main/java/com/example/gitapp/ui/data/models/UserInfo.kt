package com.example.gitapp.ui.data.models

import com.google.gson.annotations.SerializedName

data class UserInfo(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String
)
