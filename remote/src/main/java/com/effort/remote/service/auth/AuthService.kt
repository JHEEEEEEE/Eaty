package com.effort.remote.service.auth

import com.effort.data.model.auth.FirebaseUserEntity
import com.effort.remote.model.auth.FirebaseUserResponse
import kotlinx.coroutines.flow.Flow

interface AuthService {
    suspend fun authenticateUser(idToken: String): FirebaseUserResponse

    suspend fun saveUser(user: FirebaseUserEntity)

    fun observeUserUpdate(email: String): Flow<FirebaseUserResponse>
}