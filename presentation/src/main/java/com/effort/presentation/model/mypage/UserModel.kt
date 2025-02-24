package com.effort.presentation.model.mypage

import com.effort.domain.model.auth.User

data class UserModel(
    val name: String, val nickname: String, val email: String, val profilePicPath: String
)

fun User.toPresentation(): UserModel {
    return UserModel(name, nickname, email, profilePicPath)
}