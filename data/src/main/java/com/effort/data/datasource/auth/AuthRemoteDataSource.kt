package com.effort.data.datasource.auth

import com.effort.data.model.auth.FirebaseUserEntity

interface AuthRemoteDataSource {

    suspend fun authenticateUser(idToken: String): Boolean

    suspend fun saveUser(user: FirebaseUserEntity)
}