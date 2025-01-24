package com.effort.data.model.home.suggestion

import com.effort.domain.model.home.suggestion.Keyword

data class KeywordEntity(
    val keyword: String
)  {
    fun toDomain(): Keyword {
        return Keyword(keyword)
    }
}
