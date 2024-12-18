package com.effort.remote.datasourceimpl.mypage

import com.effort.data.datasource.mypage.MyPageRemoteDataSource
import com.effort.data.model.auth.FirebaseUserEntity
import com.effort.remote.service.mypage.FirebaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyPageRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : MyPageRemoteDataSource {

    override fun observeUserUpdate(email: String): Flow<FirebaseUserEntity> {
        return firebaseService.observeUserUpdate(email)
            .map { response -> response.toData() }
    }
}