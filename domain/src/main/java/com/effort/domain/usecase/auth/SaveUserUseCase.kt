package com.effort.domain.usecase.auth

import com.effort.domain.model.auth.FirebaseUser

interface SaveUserUseCase {

    suspend operator fun invoke(user: FirebaseUser)
}