package com.effort.remote.model.home.blog

import com.effort.data.model.home.blog.BlogReviewMetaEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogReviewMetaResponse(
    @SerialName("total_count") val totalCount: Int,          // 총 결과 수
    @SerialName("pageable_count") val pageableCount: Int,    // 노출 가능 결과 수
    @SerialName("is_end") val isEnd: Boolean                 // 마지막 페이지 여부
) {
    fun toData(): BlogReviewMetaEntity {
        return BlogReviewMetaEntity(totalCount, pageableCount, isEnd)
    }
}
