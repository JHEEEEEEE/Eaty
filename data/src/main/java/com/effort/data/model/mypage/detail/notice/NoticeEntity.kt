package com.effort.data.model.mypage.detail.notice

import com.effort.domain.model.mypage.detail.notice.Notice

data class NoticeEntity(
    val category: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val timestamp: String,
) {
    fun toDomain(): Notice = Notice(category, title, description, imageUrl, timestamp)
}