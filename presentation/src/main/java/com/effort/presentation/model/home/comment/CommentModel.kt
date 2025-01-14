package com.effort.presentation.model.home.comment

import com.effort.domain.model.home.comment.Comment

data class CommentModel(
    val content: String,
    val userId: String,
    val userNickname: String,
    val timestamp: String,
) {
    fun toDomain(): Comment {
        return Comment(content, userId, userNickname, timestamp)
    }
}

fun Comment.toPresentation(): CommentModel {
    return CommentModel(content, userId, userNickname, timestamp)
}