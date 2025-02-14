package com.effort.remote.model.mypage.detail.notice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeWrapperResponse (

    @SerialName("notice_results")
    val resultNotices: List<NoticeResponse>
)