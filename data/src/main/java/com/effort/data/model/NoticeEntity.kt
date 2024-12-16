package com.effort.data.model

import com.effort.domain.model.Notice

data class NoticeEntity (
    val category: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val timestamp: String,
) {
    fun toDomain(): Notice =
        Notice(category, title, description, imageUrl, timestamp)
}
