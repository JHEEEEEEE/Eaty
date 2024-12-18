package com.effort.data.datasource.auth

interface AuthRemoteDataSource {

    suspend fun authenticateUser(idToken: String): Boolean

    suspend fun checkUserLoggedIn(): Boolean
}