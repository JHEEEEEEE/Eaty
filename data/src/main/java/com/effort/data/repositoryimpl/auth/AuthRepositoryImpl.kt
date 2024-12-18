package com.effort.data.repositoryimpl.auth

import com.effort.data.datasource.auth.AuthRemoteDataSource
import com.effort.data.model.auth.toEntity
import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override fun authenticateUser(idToken: String): Flow<DataResource<FirebaseUser>> = flow {
        emit(DataResource.loading())
        try {
            val user = authRemoteDataSource.authenticateUser(idToken).toDomain()
            emit(DataResource.success(user))
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }

    override suspend fun saveUser(user: FirebaseUser) {
        authRemoteDataSource.saveUser(user.toEntity())
    }

    override fun observeUserUpdate(email: String): Flow<DataResource<FirebaseUser>> = channelFlow {
        send(DataResource.loading()) // 로딩 상태 방출

        val flow = authRemoteDataSource.observeUserUpdate(email)
            .map { entity -> DataResource.success(entity.toDomain()) }

        try {
            flow.collect { send(it) } // Flow 내 데이터 방출
        } catch (exception: Exception) {
            send(DataResource.error(exception)) // 예외 발생 시 에러 상태 방출
        }
    }
}