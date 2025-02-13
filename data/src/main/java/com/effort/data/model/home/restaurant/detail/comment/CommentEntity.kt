package com.effort.data.model.home.restaurant.detail.comment

import com.effort.domain.model.home.restaurant.detail.comment.Comment

data class CommentEntity(
    val content: String,      // 댓글 내용
    val userId: String,       // 작성자 ID
    val userNickname: String,     // 작성자 이름
    val timestamp: String,  // 작성 시간
) {
    fun toDomain(): Comment =
        Comment(content, userId, userNickname, timestamp)
}

fun Comment.toData(): CommentEntity {
    return CommentEntity(content, userId, userNickname, timestamp)
}