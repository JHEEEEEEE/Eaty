package com.effort.domain.usecaseimpl.mypage.detail.editprofile

import com.effort.domain.DataResource
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateProfilePicUseCase
import javax.inject.Inject

class UpdateProfilePicUseCaseImpl @Inject constructor(
    private val editProfileRepository: EditProfileRepository
): UpdateProfilePicUseCase {
    override suspend fun invoke(profilePicPath: String): DataResource<Boolean> {
        return editProfileRepository.updateProfilePic(profilePicPath)
    }
}