package com.effort.remote.datasourceimpl.mypage.detail.editprofile

import com.effort.data.datasource.mypage.detail.editprofile.EditProfileRemoteDataSource
import com.effort.remote.service.mypage.FirebaseService
import javax.inject.Inject

class EditProfileRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : EditProfileRemoteDataSource {

    override suspend fun updateNickname(nickname: String): Boolean {
        return firebaseService.updateNickname(nickname)
    }
}