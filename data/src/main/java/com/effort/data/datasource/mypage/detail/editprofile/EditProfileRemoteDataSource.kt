package com.effort.data.datasource.mypage.detail.editprofile

interface EditProfileRemoteDataSource {

    suspend fun updateNickname(nickname: String): Boolean
}