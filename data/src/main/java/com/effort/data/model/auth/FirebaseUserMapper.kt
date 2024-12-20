package com.effort.data.model.auth

import com.effort.domain.model.auth.FirebaseUser

// Domain → Data 변환
fun FirebaseUser.toEntity(): FirebaseUserEntity {
    return FirebaseUserEntity(
        name = this.name,
        nickname = this.nickname,
        email = this.email,
        profilePicPath = this.profilePicPath
    )
}