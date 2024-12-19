package com.effort.domain.usecase.mypage.detail.editprofile

import com.effort.domain.DataResource
import kotlinx.coroutines.flow.Flow

interface UpdateNicknameUseCase {

    suspend operator fun invoke(nickname: String): DataResource<Boolean>
}