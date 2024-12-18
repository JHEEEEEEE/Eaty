package com.effort.domain.repository.auth

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser

interface AuthRepository {

    suspend fun authenticateUser(idToken: String): DataResource<Boolean>

    suspend fun saveUser(user: FirebaseUser)
}