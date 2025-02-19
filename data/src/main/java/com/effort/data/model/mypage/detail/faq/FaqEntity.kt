package com.effort.data.model.mypage.detail.faq

import com.effort.domain.model.mypage.detail.faq.Faq

data class FaqEntity(
    val category: String,
    val question: String,
    val answer: String,
    val timestamp: String,
) {
    fun toDomain(): Faq = Faq(category, question, answer, timestamp)
}