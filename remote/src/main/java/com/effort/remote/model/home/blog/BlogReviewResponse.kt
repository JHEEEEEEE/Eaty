package com.effort.remote.model.home.blog

import com.effort.data.model.home.blog.BlogReviewEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogReviewResponse(
    @SerialName("title") val title: String = "",
    @SerialName("contents") val contents: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("thumbnail") val thumbnail: String = "",
    @SerialName("datetime") val dateTime: String = "",
)  {
    fun toData(): BlogReviewEntity {
        return BlogReviewEntity(title, contents, url, dateTime)
    }
}