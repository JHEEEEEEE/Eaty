package com.effort.remote.model.home

import com.effort.data.model.home.RestaurantEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RestaurantResponse(
    @SerialName("title") val title: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("roadAddress") val roadAddress: String = "",
    @SerialName("mapx") val mapx: String = "",
    @SerialName("mapy") val mapy: String = ""
) {
    fun toData(): RestaurantEntity {
        return RestaurantEntity(title)
    }
}
