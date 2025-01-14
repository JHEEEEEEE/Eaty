package com.effort.remote.datasourceimpl.mypage.detail.editprofile

import com.effort.data.datasource.mypage.detail.editprofile.EditProfileRemoteDataSource
import com.effort.remote.service.mypage.MyPageService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditProfileRemoteDataSourceImpl @Inject constructor(
    private val myPageService: MyPageService
) : EditProfileRemoteDataSource {

    override suspend fun updateNickname(nickname: String): Boolean {
        return myPageService.updateNickname(nickname)
    }

    override fun checkNicknameDuplicated(nickname: String): Flow<Boolean> {
        return myPageService.checkNicknameDuplicated(nickname)
    }

    override suspend fun updateProfilePic(profilePicPath: String): Boolean {
        return myPageService.updateProfilePic(profilePicPath)
    }
}