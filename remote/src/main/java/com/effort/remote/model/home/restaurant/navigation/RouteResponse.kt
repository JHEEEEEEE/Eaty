package com.effort.remote.model.home.restaurant.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* "route" 키 아래 포함된 데이터 (경로 관련 정보) */
@Serializable
data class RouteResponse(
    @SerialName("trafast")
    val trafast: List<TraFastResponse> // "가장 빠른 길"을 포함하는 리스트
)