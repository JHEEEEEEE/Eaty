package com.effort.domain.repository.auth

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun authenticateUser(idToken: String): Flow<DataResource<FirebaseUser>>

    suspend fun saveUser(user: FirebaseUser)

    fun observeUserUpdate(email: String): Flow<DataResource<FirebaseUser>>
}