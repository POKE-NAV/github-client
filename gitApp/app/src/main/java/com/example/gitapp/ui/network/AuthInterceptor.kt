package com.example.gitapp.ui.network

import com.example.gitapp.ui.data.storage.KeyValueStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val storage: KeyValueStorage
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = storage.authToken

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "GitHubClient-Android")

        if(!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        if (response.code == 401) {
            storage.authToken = null
        }
        return response
    }
}