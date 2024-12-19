package com.effort.data.datasource.mypage.detail.editprofile

import kotlinx.coroutines.flow.Flow

interface EditProfileRemoteDataSource {

    suspend fun updateNickname(nickname: String): Boolean

    fun checkNicknameDuplicated(nickname: String): Flow<Boolean>
}