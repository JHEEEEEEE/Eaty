package com.effort.presentation.model.mypage.detail.notice

import com.effort.domain.model.mypage.detail.notice.Notice

data class NoticeModel(
    val category: String,
    val title: String,
    val description: String,
    val imageUrl: String,
)

fun Notice.toPresentation(): NoticeModel {
    return NoticeModel(
        category = this.category,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl
    )
}