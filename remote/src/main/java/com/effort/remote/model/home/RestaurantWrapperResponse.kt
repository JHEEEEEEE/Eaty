package com.effort.remote.model.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantWrapperResponse(
    @SerialName("documents") // JSON 필드 매핑
    val documents: List<RestaurantResponse>
)