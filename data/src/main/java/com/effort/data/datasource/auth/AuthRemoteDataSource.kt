package com.effort.data.datasource.auth

import com.effort.data.model.auth.FirebaseUserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {

    suspend fun authenticateUser(idToken: String): FirebaseUserEntity

    suspend fun saveUser(user: FirebaseUserEntity)

    fun observeUserUpdate(email: String): Flow<FirebaseUserEntity>
}