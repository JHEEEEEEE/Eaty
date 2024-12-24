package com.effort.remote.datasourceimpl.mypage.detail.editprofile

import com.effort.data.datasource.mypage.detail.editprofile.EditProfileRemoteDataSource
import com.effort.remote.service.mypage.FirebaseService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditProfileRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : EditProfileRemoteDataSource {

    override suspend fun updateNickname(nickname: String): Boolean {
        return firebaseService.updateNickname(nickname)
    }

    override fun checkNicknameDuplicated(nickname: String): Flow<Boolean> {
        return firebaseService.checkNicknameDuplicated(nickname)
    }

    override suspend fun updateProfilePic(profilePicPath: String): Boolean {
        return firebaseService.updateProfilePic(profilePicPath)
    }
}