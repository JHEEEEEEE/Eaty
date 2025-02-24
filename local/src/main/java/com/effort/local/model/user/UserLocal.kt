package com.effort.local.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.effort.data.model.auth.UserEntity

@Entity(tableName = "user")
data class UserLocal(
    @PrimaryKey val email: String,
    val name: String,
    var nickname: String?,
    var profilePicPath: String,
) {
    fun toData(): UserEntity {
        return UserEntity(name, nickname ?: "", email, profilePicPath)
    }
}

// UserEntity 확장 함수
fun UserEntity.toLocal(): UserLocal {
    return UserLocal(
        email = this.email,
        name = this.name,
        nickname = this.nickname,
        profilePicPath = this.profilePicPath
    )
}