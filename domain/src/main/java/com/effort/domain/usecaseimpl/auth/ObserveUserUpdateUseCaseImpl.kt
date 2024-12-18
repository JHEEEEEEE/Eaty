package com.effort.domain.usecaseimpl.auth

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.usecase.auth.ObserveUserUpdateUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserUpdateUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): ObserveUserUpdateUseCase {

    override fun invoke(email: String): Flow<DataResource<FirebaseUser>> {
        return authRepository.observeUserUpdate(email)
    }
}