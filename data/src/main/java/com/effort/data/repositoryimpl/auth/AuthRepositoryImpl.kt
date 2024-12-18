package com.effort.data.repositoryimpl.auth

import com.effort.data.datasource.auth.AuthRemoteDataSource
import com.effort.data.model.auth.toEntity
import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun authenticateUser(idToken: String): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()
            val isAuthenticated = authRemoteDataSource.authenticateUser(idToken)
            DataResource.success(isAuthenticated)
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override suspend fun checkUserLoggedIn(): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()
            val isLoggedIn = authRemoteDataSource.checkUserLoggedIn()
            DataResource.success(isLoggedIn)
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}