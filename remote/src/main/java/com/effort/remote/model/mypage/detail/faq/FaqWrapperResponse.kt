package com.effort.remote.model.mypage.detail.faq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Firestore에서 가져온 FaqResponse 리스트를 감싸는 Wrapper 클래스
// 데이터를 List<FaqResponse> 형태로 처리하고 summaryFaqs 필드를 통해 접근 가능
@Serializable
data class FaqWrapperResponse(

    @SerialName("faq_results") // JSON 필드 매핑
    val resultFaqs: List<FaqResponse>
)