package com.effort.remote.model.mypage.detail.notice

import com.effort.data.model.mypage.detail.notice.NoticeEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeResponse (

    @SerialName("category")
    val category: String = "",

    @SerialName("title")
    val title: String = "",

    @SerialName("description")
    val description: String = "",

    @SerialName("imageUrl")
    val imageUrl: String = "",

    @SerialName("timestamp")
    val timestamp: String = "",
) {
    fun toData(): NoticeEntity =
        NoticeEntity(category, title, description, imageUrl, timestamp)
}