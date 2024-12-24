package com.effort.domain.usecase.mypage.detail.editprofile

import com.effort.domain.DataResource
import kotlinx.coroutines.flow.Flow

interface CheckNicknameDuplicatedUseCase {

    operator fun invoke(nickname: String): Flow<DataResource<Boolean>>
}