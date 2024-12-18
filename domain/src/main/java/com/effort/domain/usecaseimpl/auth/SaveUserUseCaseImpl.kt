package com.effort.domain.usecaseimpl.auth

import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.usecase.auth.SaveUserUseCase
import javax.inject.Inject

class SaveUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SaveUserUseCase {

    override suspend fun invoke(user: FirebaseUser) {
        authRepository.saveUser(user)
    }
}