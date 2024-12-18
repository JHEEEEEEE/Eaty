package com.effort.data.datasource.mypage

import com.effort.data.model.auth.FirebaseUserEntity
import kotlinx.coroutines.flow.Flow

interface MyPageRemoteDataSource {

    fun observeUserUpdate(email: String): Flow<FirebaseUserEntity>
}