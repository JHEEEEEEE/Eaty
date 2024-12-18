package com.effort.domain.usecaseimpl.auth

import com.effort.domain.DataResource
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.usecase.auth.CheckUserLoggedInUseCase
import javax.inject.Inject

class CheckUserLoggedInUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): CheckUserLoggedInUseCase {

    override suspend fun invoke(): DataResource<Boolean> {
        return authRepository.checkUserLoggedIn()
    }
}