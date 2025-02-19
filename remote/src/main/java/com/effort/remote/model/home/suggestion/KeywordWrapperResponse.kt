package com.effort.remote.model.home.suggestion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeywordWrapperResponse(

    @SerialName("keyword_results") val resultKeywords: List<KeywordResponse>
)