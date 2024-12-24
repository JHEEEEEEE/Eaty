package com.effort.domain.usecaseimpl.mypage.detail.editprofile

import com.effort.domain.DataResource
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateNicknameUseCase
import javax.inject.Inject

class UpdateNicknameUseCaseImpl @Inject constructor(
    private val editProfileRepository: EditProfileRepository
): UpdateNicknameUseCase {

    override suspend fun invoke(nickname: String): DataResource<Boolean> {
        return editProfileRepository.updateNickname(nickname)
    }
}