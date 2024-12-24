package com.effort.domain.usecase.mypage.detail.editprofile

import com.effort.domain.DataResource

interface UpdateProfilePicUseCase {

    suspend operator fun invoke(profilePicPath: String): DataResource<Boolean>
}