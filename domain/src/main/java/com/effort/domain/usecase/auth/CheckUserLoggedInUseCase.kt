package com.effort.domain.usecase.auth

import com.effort.domain.DataResource

interface CheckUserLoggedInUseCase {

    suspend operator fun invoke(): DataResource<Boolean>
}