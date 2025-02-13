package com.effort.domain.model.home.restaurant.detail.comment

data class Comment(
    val content: String,      // 댓글 내용
    val userId: String,       // 작성자 ID
    val userNickname: String,     // 작성자 이름
    val timestamp: String,  // 작성 시간
)