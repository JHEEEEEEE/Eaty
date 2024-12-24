package com.effort.presentation.model.auth

import com.effort.domain.model.auth.User

data class FirebaseUserModel(
    val name: String,
    val nickname: String,
    val email: String,
    val profilePicPath: String
)

fun User.toPresentation(): FirebaseUserModel {
    return FirebaseUserModel(name, nickname, email, profilePicPath)
}