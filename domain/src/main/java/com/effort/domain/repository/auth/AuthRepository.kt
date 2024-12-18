package com.effort.domain.repository.auth

import com.effort.domain.DataResource

interface AuthRepository {

    suspend fun authenticateUser(idToken: String): DataResource<Boolean>

    suspend fun checkUserLoggedIn(): DataResource<Boolean>
}