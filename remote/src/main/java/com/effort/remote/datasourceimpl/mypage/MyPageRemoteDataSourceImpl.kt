package com.effort.remote.datasourceimpl.mypage

import com.effort.data.datasource.mypage.MyPageRemoteDataSource
import com.effort.data.model.auth.UserEntity
import com.effort.remote.service.mypage.MyPageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyPageRemoteDataSourceImpl @Inject constructor(
    private val myPageService: MyPageService
) : MyPageRemoteDataSource {

    override fun observeUserUpdate(): Flow<UserEntity> {
        return myPageService.observeUserUpdate()
            .map { response -> response.toData() }
    }
}