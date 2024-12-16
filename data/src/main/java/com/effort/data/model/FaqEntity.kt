package com.effort.data.model

import com.effort.domain.model.Faq

data class FaqEntity (
    val category: String,
    val question: String,
    val answer: String,
    val timestamp: String,
) {
    fun toDomain(): Faq =
        Faq(category, question, answer, timestamp)
}