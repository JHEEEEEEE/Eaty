package com.effort.domain.usecaseimpl.mypage.detail.editprofile

import com.effort.domain.DataResource
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import com.effort.domain.usecase.mypage.detail.editprofile.CheckNicknameDuplicatedUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckNicknameDuplicatedUseCaseImpl @Inject constructor(
    private val editProfileRepository: EditProfileRepository
): CheckNicknameDuplicatedUseCase {

    override fun invoke(nickname: String): Flow<DataResource<Boolean>> {
        return editProfileRepository.checkNicknameDuplicated(nickname)
    }
}