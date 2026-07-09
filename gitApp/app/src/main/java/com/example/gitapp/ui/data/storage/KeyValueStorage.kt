package com.example.gitapp.ui.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class KeyValueStorage(
    private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
    }

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        set(value) = prefs.edit { putString("auth_token", value) }

    var userLogin: String?
        get() = prefs.getString("user_login", null)
        set(value) = prefs.edit { putString("user_login", value) }

    fun clearAll() {
        authToken = null
        userLogin = null
    }
}