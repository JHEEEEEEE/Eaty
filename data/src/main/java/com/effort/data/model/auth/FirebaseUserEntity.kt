package com.effort.data.model.auth

import com.effort.domain.model.auth.FirebaseUser

data class FirebaseUserEntity(
    val name: String,
    val nickname: String,
    val email: String,
    val profilePicUrl: String,
) {
    fun toDomain(): FirebaseUser =
        FirebaseUser(name, nickname, email, profilePicUrl)
}
