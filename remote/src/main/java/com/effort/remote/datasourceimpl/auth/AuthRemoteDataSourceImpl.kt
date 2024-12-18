package com.effort.remote.datasourceimpl.auth

import com.effort.data.datasource.auth.AuthRemoteDataSource
import com.effort.data.model.auth.FirebaseUserEntity
import com.effort.remote.service.auth.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : AuthRemoteDataSource {
    override suspend fun authenticateUser(idToken: String): FirebaseUserEntity {
        val response = authService.authenticateUser(idToken)
        return response.toData() // FirebaseUserResponse → FirebaseUserEntity 변환
    }

    override suspend fun saveUser(user: FirebaseUserEntity) {
        authService.saveUser(user)
    }

    override fun observeUserUpdate(email: String): Flow<FirebaseUserEntity> {
        return authService.observeUserUpdate(email)
            .map { response -> response.toData() }
    }
}