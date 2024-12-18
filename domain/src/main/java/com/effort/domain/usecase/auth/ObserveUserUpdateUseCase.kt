package com.effort.domain.usecase.auth

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface ObserveUserUpdateUseCase {

    operator fun invoke(email: String): Flow<DataResource<FirebaseUser>>
}