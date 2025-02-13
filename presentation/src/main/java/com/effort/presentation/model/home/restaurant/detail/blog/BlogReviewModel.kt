package com.effort.presentation.model.home.restaurant.detail.blog

import com.effort.domain.model.home.restaurant.detail.blog.BlogReview

data class BlogReviewModel(
    val title: String,
    val contents: String,
    val url: String,
    val dateTime: String
)

fun BlogReview.toPresentation(): BlogReviewModel {
    return BlogReviewModel(
        title, contents, url, dateTime
    )
}