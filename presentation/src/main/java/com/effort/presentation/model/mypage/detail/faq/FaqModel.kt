package com.effort.presentation.model.mypage.detail.faq

import com.effort.domain.model.mypage.detail.faq.Faq

data class FaqModel(
    val category: String,
    val question: String,
    val answer: String
)

fun Faq.toPresentation(): FaqModel {
    return FaqModel(
        category = this.category,
        question = this.question,
        answer = this.answer
    )
}