package com.effort.data.datasource.mypage

import com.effort.data.model.auth.UserEntity

interface MyPageLocalDataSource {

    suspend fun getUser(): UserEntity?

    suspend fun saveUser(userEntity: UserEntity)
}