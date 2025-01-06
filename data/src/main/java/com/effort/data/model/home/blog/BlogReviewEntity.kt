package com.effort.data.model.home.blog

import com.effort.domain.model.home.blog.BlogReview

data class BlogReviewEntity (
    val title: String,
    val contents: String,
    val url: String,
    val dateTime: String
) {
    fun toDomain(): BlogReview {
        return BlogReview(title, contents, url, dateTime)
    }
}