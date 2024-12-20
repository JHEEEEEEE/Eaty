package com.effort.domain.repository.mypage.detail.editprofile

import com.effort.domain.DataResource
import kotlinx.coroutines.flow.Flow

interface EditProfileRepository {

    suspend fun updateNickname(nickname: String): DataResource<Boolean>

    fun checkNicknameDuplicated(nickname: String): Flow<DataResource<Boolean>>

    suspend fun updateProfilePic(profilePicPath: String): DataResource<Boolean>
}