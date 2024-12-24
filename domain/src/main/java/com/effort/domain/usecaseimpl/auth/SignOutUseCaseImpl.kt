package com.effort.domain.usecaseimpl.auth

import com.effort.domain.DataResource
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.usecase.auth.SignOutUseCase
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SignOutUseCase {
    override suspend fun invoke(): DataResource<Boolean> {
        return authRepository.signOut()
    }
}