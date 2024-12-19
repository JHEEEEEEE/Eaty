package com.effort.domain.usecase.mypage.detail.editprofile

import com.effort.domain.DataResource

interface UpdateNicknameUseCase {

    suspend operator fun invoke(nickname: String): DataResource<Boolean>
}