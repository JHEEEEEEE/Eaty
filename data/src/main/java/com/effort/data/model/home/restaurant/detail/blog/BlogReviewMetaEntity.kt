package com.effort.data.model.home.restaurant.detail.blog

import com.effort.domain.model.home.restaurant.detail.blog.BlogReviewMeta

data class BlogReviewMetaEntity(
    val totalCount: Int,
    val pageableCount: Int,
    val isEnd: Boolean
) {
    fun toDomain(): BlogReviewMeta {
        return BlogReviewMeta(totalCount, pageableCount, isEnd)
    }
}