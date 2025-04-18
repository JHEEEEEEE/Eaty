package com.effort.domain.usecaseimpl.mypage

import com.effort.domain.DataResource
import com.effort.domain.model.auth.User
import com.effort.domain.repository.mypage.MyPageRepository
import com.effort.domain.usecase.mypage.ObserveUserUpdateUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserUpdateUseCaseImpl @Inject constructor(
    private val myPageRepository: MyPageRepository
): ObserveUserUpdateUseCase {

    override fun invoke(): Flow<DataResource<User>> {
        return myPageRepository.observeUserUpdate()
    }
}