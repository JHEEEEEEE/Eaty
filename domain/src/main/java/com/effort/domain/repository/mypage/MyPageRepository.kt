package com.effort.domain.repository.mypage

import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface MyPageRepository {

    fun observeUserUpdate(email: String): Flow<DataResource<FirebaseUser>>
}