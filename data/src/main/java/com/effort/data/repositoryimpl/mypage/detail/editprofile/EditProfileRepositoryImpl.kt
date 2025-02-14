package com.effort.data.repositoryimpl.mypage.detail.editprofile

import com.effort.data.datasource.mypage.detail.editprofile.EditProfileRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditProfileRepositoryImpl @Inject constructor(
    private val editProfileRemoteDataSource: EditProfileRemoteDataSource
) : EditProfileRepository {

    override suspend fun updateNickname(nickname: String): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()

            val isNicknameUpdated = editProfileRemoteDataSource.updateNickname(nickname)
            DataResource.success(isNicknameUpdated)
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override fun checkNicknameDuplicated(nickname: String): Flow<DataResource<Boolean>> = flow {
        emit(DataResource.loading()) // 로딩 상태 방출

        try {
            editProfileRemoteDataSource.checkNicknameDuplicated(nickname)
                .collect { isAvailable ->
                    emit(DataResource.success(isAvailable)) // 성공 상태 방출
                }
        } catch (e: Exception) {
            emit(DataResource.error(e)) // 에러 상태 방출
        }
    }

    override suspend fun updateProfilePic(profilePicPath: String): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()

            val isProfilePicUpdated = editProfileRemoteDataSource.updateProfilePic(profilePicPath)
            DataResource.Success(isProfilePicUpdated)
        } catch (e: Exception) {
            DataResource.Error(e)
        }
    }
}