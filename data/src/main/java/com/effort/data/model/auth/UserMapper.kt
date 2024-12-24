package com.effort.data.model.auth

import com.effort.domain.model.auth.User

// Domain → Data 변환
fun User.toEntity(): UserEntity {
    return UserEntity(
        name = this.name,
        nickname = this.nickname,
        email = this.email,
        profilePicPath = this.profilePicPath
    )
}
