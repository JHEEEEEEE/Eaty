package com.effort.data.repositoryimpl.auth

import com.effort.data.datasource.auth.AuthLocalDataSource
import com.effort.data.datasource.auth.AuthRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.auth.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {

    override suspend fun authenticateUser(idToken: String): DataResource<Boolean> {
        Timber.d("authenticateUser() 호출됨")
        return try {
            DataResource.loading<Boolean>()
            val isAuthenticated = authRemoteDataSource.authenticateUser(idToken)
            Timber.d("authenticateUser() 성공: $isAuthenticated")
            DataResource.success(isAuthenticated)
        } catch (e: Exception) {
            Timber.e(e, "authenticateUser() 실패")
            DataResource.error(e)
        }
    }

    override suspend fun checkUserLoggedIn(): DataResource<Boolean> {
        Timber.d("checkUserLoggedIn() 호출됨")
        return try {
            DataResource.loading<Boolean>()
            val isLoggedIn = authRemoteDataSource.checkUserLoggedIn()
            Timber.d("checkUserLoggedIn() 결과: $isLoggedIn")
            DataResource.success(isLoggedIn)
        } catch (e: Exception) {
            Timber.e(e, "checkUserLoggedIn() 실패")
            DataResource.error(e)
        }
    }

    override suspend fun signOut(): DataResource<Boolean> {
        Timber.d("signOut() 호출됨")
        return try {
            DataResource.Loading<Boolean>()

            val isSignedOut = authRemoteDataSource.signOut()
            Timber.d("signOut() 성공 여부: $isSignedOut")

            try {
                authLocalDataSource.clearUsers()
                Timber.d("로컬 사용자 데이터 삭제 완료")
            } catch (e: Exception) {
                Timber.e(e, "로컬 데이터 삭제 실패")
            }

            DataResource.success(isSignedOut)
        } catch (e: Exception) {
            Timber.e(e, "signOut() 실패")
            DataResource.error(e)
        }
    }
}