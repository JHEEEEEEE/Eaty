package com.effort.data.repositoryimpl.mypage

import android.util.Log
import com.effort.data.datasource.mypage.MyPageLocalDataSource
import com.effort.data.datasource.mypage.MyPageRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.auth.User
import com.effort.domain.repository.mypage.MyPageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageRemoteDataSource: MyPageRemoteDataSource,
    private val myPageLocalDataSource: MyPageLocalDataSource
) : MyPageRepository {

    override fun observeUserUpdate(): Flow<DataResource<User>> = channelFlow {
        try {
            val localData = myPageLocalDataSource.getUser()
            localData?.let {
                send(DataResource.success(it.toDomain()))
            }
        } catch (e: Exception) {
            Log.e("MyPageRepositoryImpl", "Failed to load local data: ${e.message}")
        }

        myPageRemoteDataSource.observeUserUpdate()
            .retry(2)
            .map { entity ->
                val domainData = entity.toDomain()

                try {
                    myPageLocalDataSource.saveUser(entity)
                } catch (e: Exception) {
                    Log.e("MyPageRepositoryImpl", "Failed to save user data: ${e.message}")
                }

                DataResource.success(domainData) // 최신 데이터 반환
            }
            .catch { e ->
                Log.e("MyPageRepositoryImpl", "Failed to fetch remote data: ${e.message}")
                val fallbackData = myPageLocalDataSource.getUser()
                if (fallbackData != null) {
                    send(DataResource.success(fallbackData.toDomain())) // 로컬 데이터 복구
                } else {
                    send(DataResource.error(e)) // 최종 에러 반환
                }
            }
            .collect { send(it) }
    }
}
