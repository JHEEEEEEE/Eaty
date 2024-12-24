package com.effort.domain.usecaseimpl.auth

import com.effort.domain.DataResource
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import javax.inject.Inject

class AuthenticateUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): AuthenticateUserUseCase {

    override suspend operator fun invoke(idToken: String): DataResource<Boolean> {
        return authRepository.authenticateUser(idToken)
    }
}