package com.effort.remote.datasourceimpl.auth

import com.effort.data.datasource.auth.AuthRemoteDataSource
import com.effort.remote.service.auth.AuthService
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : AuthRemoteDataSource {

    override suspend fun authenticateUser(idToken: String): Boolean {
        val response = authService.authenticateUser(idToken)
        return response // FirebaseUserResponse → FirebaseUserEntity 변환
    }

    override suspend fun checkUserLoggedIn(): Boolean {
        return authService.checkUserLoggedIn()
    }
}