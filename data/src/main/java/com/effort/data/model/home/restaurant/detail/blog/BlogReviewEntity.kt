package com.effort.data.model.home.restaurant.detail.blog

import com.effort.domain.model.home.restaurant.detail.blog.BlogReview

data class BlogReviewEntity(
    val title: String,
    val contents: String,
    val url: String,
    val dateTime: String
) {
    fun toDomain(): BlogReview {
        return BlogReview(title, contents, url, dateTime)
    }
}