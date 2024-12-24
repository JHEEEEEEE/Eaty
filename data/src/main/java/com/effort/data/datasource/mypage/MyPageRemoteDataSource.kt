package com.effort.data.datasource.mypage

import com.effort.data.model.auth.UserEntity
import kotlinx.coroutines.flow.Flow

interface MyPageRemoteDataSource {

    fun observeUserUpdate(): Flow<UserEntity>
}