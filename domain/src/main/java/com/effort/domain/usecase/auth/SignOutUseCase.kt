package com.effort.domain.usecase.auth

import com.effort.domain.DataResource

interface SignOutUseCase {

    suspend operator fun invoke(): DataResource<Boolean>
}
