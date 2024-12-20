package com.effort.remote.model.auth

import com.effort.data.model.auth.FirebaseUserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUserResponse(
    @SerialName("name")
    val name: String = "",

    @SerialName("nickname")
    val nickname: String = "",

    @SerialName("email")
    val email: String = "",

    @SerialName("profilePic")
    val profilePicPath: String = "",
) {
    fun toData(): FirebaseUserEntity =
        FirebaseUserEntity(name, nickname, email, profilePicPath)
}
