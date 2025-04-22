package com.effort.data.model.auth

import com.effort.domain.model.auth.User

data class UserEntity(
    val name: String,
    val nickname: String,
    val email: String,
    val profilePicPath: String,
) {
    fun toDomain(): User = User(name, nickname, email, profilePicPath)
}