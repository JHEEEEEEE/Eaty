package com.effort.data.repositoryimpl.mypage

import com.effort.data.datasource.mypage.MyPageRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.repository.mypage.MyPageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageRemoteDataSource: MyPageRemoteDataSource
) : MyPageRepository {

    override fun observeUserUpdate(): Flow<DataResource<FirebaseUser>> = channelFlow {
        send(DataResource.loading())

        val flow = myPageRemoteDataSource.observeUserUpdate()
            .map { entity -> DataResource.success(entity.toDomain()) }

        try {
            flow.collect { send(it) } // Flow 내 데이터 방출
        } catch (exception: Exception) {
            send(DataResource.error(exception))
        }
    }
}