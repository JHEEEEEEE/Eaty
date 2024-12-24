package com.effort.remote.service.auth

interface AuthService {

    suspend fun authenticateUser(idToken: String): Boolean

    suspend fun checkUserLoggedIn(): Boolean

    suspend fun signOut(): Boolean
}