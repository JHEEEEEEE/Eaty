package com.effort.domain.model.auth

data class FirebaseUser(
    val name: String,
    val nickname: String,
    val email: String,
    val profilePicPath: String
)