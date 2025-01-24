package com.effort.presentation.model.home

import com.effort.domain.model.home.suggestion.Keyword

data class KeywordModel(
    val keyword: String
)

fun Keyword.toPresentation(): KeywordModel {
    return KeywordModel(keyword)
}