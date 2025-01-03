package com.effort.remote.model.home

import com.effort.data.model.home.RestaurantMetaEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMetaResponse(
    @SerialName("total_count") val totalCount: Int,          // 총 결과 수
    @SerialName("pageable_count") val pageableCount: Int,    // 노출 가능 결과 수
    @SerialName("is_end") val isEnd: Boolean                 // 마지막 페이지 여부
) {
    // Data Layer의 RestaurantEntity로 변환
    fun toData(): RestaurantMetaEntity {
        return RestaurantMetaEntity(
            totalCount = totalCount,
            pageableCount = pageableCount,
            isEnd = isEnd
        )
    }
}
