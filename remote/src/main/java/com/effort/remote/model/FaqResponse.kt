package com.effort.remote.model

import com.effort.data.model.FaqEntity
import com.google.firebase.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Firestore에서 가져온 데이터를 나타내는 DTO. Raw 데이터를 표현
// Firestore의 JSON 구조와 매핑되며, toData() 메서드를 통해 Domain 계층의 Entity로 변환 가능
@Serializable
data class FaqResponse(
    @SerialName("category")
    val category: String = "",
    @SerialName("question")
    val question: String = "",
    @SerialName("answer")
    val answer: String = "",
    @SerialName("timestamp")
    val timestamp: String = ""
) {
    // FaqResponse를 Domain 계층에서 사용하는 FaqEntity로 변환
    fun toData(): FaqEntity =
        FaqEntity(category, question, answer, timestamp)
}