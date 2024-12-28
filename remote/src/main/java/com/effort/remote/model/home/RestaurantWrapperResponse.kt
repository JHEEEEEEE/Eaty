package com.effort.remote.model.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantWrapperResponse(
    @SerialName("items") // JSON 필드 매핑
    val items: List<RestaurantResponse>
)