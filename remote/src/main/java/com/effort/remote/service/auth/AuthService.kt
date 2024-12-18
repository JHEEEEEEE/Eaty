package com.effort.remote.service.auth

import com.effort.data.model.auth.FirebaseUserEntity
import com.effort.remote.model.auth.FirebaseUserResponse

interface AuthService {
    suspend fun authenticateUser(idToken: String): Boolean

    suspend fun saveUser(user: FirebaseUserEntity)
}