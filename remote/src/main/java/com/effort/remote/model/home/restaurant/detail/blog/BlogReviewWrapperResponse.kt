package com.effort.remote.model.home.restaurant.detail.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogReviewWrapperResponse(
    @SerialName("meta")
    val meta: BlogReviewMetaResponse,

    @SerialName("documents") // JSON 필드 매핑
    val documents: List<BlogReviewResponse>
)
