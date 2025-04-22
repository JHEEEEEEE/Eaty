package com.effort.remote.model.home.suggestion

import com.effort.data.model.home.suggestion.KeywordEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeywordResponse(

    @SerialName("keyword") val keyword: String
) {
    fun toData(): KeywordEntity {
        return KeywordEntity(keyword)
    }
}