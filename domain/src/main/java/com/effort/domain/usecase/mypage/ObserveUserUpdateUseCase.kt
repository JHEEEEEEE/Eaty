package com.effort.domain.usecase.mypage

import com.effort.domain.DataResource
import com.effort.domain.model.auth.User
import kotlinx.coroutines.flow.Flow

interface ObserveUserUpdateUseCase {

    operator fun invoke(): Flow<DataResource<User>>
}