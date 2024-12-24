package com.effort.domain.usecase.auth

import com.effort.domain.DataResource

interface AuthenticateUserUseCase {

    suspend operator fun invoke(idToken: String): DataResource<Boolean>
}
