package com.effort.domain.usecaseimpl.auth

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticateUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): AuthenticateUserUseCase {

    override operator fun invoke(idToken: String): Flow<DataResource<FirebaseUser>> {
        return authRepository.authenticateUser(idToken)
    }
}