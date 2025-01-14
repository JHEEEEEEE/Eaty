package com.effort.remote.model.home.comment

import com.effort.data.model.home.comment.CommentEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("content")
    val content: String = "",

    @SerialName("userId")
    val userId: String = "",

    @SerialName("userNickname")
    val userNickname: String = "",

    @SerialName("timestamp")
    val timestamp: String = ""
) {
    fun toData(): CommentEntity {
        return CommentEntity(content, userId, userNickname, timestamp)
    }
}

fun CommentEntity.toRemote(): CommentResponse {
    return CommentResponse(content, userId, userNickname, timestamp)
}
