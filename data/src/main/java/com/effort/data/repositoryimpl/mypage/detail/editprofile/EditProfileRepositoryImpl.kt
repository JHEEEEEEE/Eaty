package com.effort.data.repositoryimpl.mypage.detail.editprofile

import com.effort.data.datasource.mypage.detail.editprofile.EditProfileRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import javax.inject.Inject

class EditProfileRepositoryImpl @Inject constructor(
    private val editProfileRemoteDataSource: EditProfileRemoteDataSource
): EditProfileRepository {

    override suspend fun updateNickname(nickname: String): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()
            val isUpdated = editProfileRemoteDataSource.updateNickname(nickname)
            DataResource.success(isUpdated)
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

}