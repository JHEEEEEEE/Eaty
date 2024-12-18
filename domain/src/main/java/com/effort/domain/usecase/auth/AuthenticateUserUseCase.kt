package com.effort.domain.usecase.auth

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthenticateUserUseCase {

    operator fun invoke(idToken: String): Flow<DataResource<FirebaseUser>>
}
