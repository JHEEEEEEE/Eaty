package com.effort.domain.model.auth

data class User(
    val name: String,
    val nickname: String,
    val email: String,
    val profilePicPath: String
)