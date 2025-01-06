package com.effort.presentation.model.home.blog

import com.effort.domain.model.home.blog.BlogReview

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

