package com.effort.domain.repository.mypage

import com.effort.domain.DataResource
import com.effort.domain.model.auth.User
import kotlinx.coroutines.flow.Flow

interface MyPageRepository {

    fun observeUserUpdate(): Flow<DataResource<User>>
}