package com.effort.presentation.model.auth

import com.effort.domain.model.auth.FirebaseUser

data class FirebaseUserModel(
    val name: String,
    val nickname: String,
    val email: String,
    val profilePicPath: String
)

fun FirebaseUser.toPresentation(): FirebaseUserModel {
    return FirebaseUserModel(name, nickname, email, profilePicPath)
}