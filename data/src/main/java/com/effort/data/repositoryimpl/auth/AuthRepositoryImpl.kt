package com.effort.data.repositoryimpl.auth

import android.util.Log
import com.effort.data.datasource.auth.AuthLocalDataSource
import com.effort.data.datasource.auth.AuthRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource
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

    override suspend fun signOut(): DataResource<Boolean> {
        return try {
            DataResource.Loading<Boolean>()

            val isSignedOut = authRemoteDataSource.signOut()

            try {
                authLocalDataSource.clearUsers()
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Failed to clear local data: ${e.message}")
            }

            DataResource.success(isSignedOut)
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}