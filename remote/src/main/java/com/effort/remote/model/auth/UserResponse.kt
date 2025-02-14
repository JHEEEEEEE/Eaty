package com.effort.remote.model.auth

import com.effort.data.model.auth.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(

    @SerialName("name")
    val name: String = "",

    @SerialName("nickname")
    val nickname: String = "",

    @SerialName("email")
    val email: String = "",

    @SerialName("profilePic")
    val profilePicPath: String = "",
) {
    fun toData(): UserEntity =
        UserEntity(name, nickname, email, profilePicPath)
}
