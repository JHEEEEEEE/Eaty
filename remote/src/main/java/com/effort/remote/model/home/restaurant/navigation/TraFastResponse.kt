package com.effort.remote.model.home.restaurant.navigation

import com.effort.data.model.home.restaurant.navigation.NavigationPathEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* "trafast" 데이터 (가장 빠른 경로) */
@Serializable
data class TraFastResponse(
    @SerialName("path")
    val path: List<List<Double>> // `[longitude, latitude]` 형식의 경로 좌표 리스트
) {
    fun toData(): List<NavigationPathEntity> {
        return path.map { point ->
            NavigationPathEntity(latitude = point[1], longitude = point[0]) // ✅ 위도, 경도 순서로 변환
        }
    }
}