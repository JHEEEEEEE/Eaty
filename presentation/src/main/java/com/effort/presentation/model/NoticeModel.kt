package com.effort.presentation.model

import com.effort.domain.model.Notice

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
