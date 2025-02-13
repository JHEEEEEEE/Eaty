package com.effort.remote.model.home.restaurant.detail.subway

import com.effort.data.model.home.restaurant.detail.subway.SubwayEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubwayResponse(
    @SerialName("place_name") val title: String = "",          // 장소명
    @SerialName("address_name") val lotNumberAddress: String = "",      // 지번 주소
    @SerialName("road_address_name") val roadNameAddress: String = "", // 도로명 주소
    @SerialName("phone") val phoneNumber: String = "",
    @SerialName("place_url") val placeUrl: String = "",
    @SerialName("distance") val distance: String = "",
    @SerialName("x") val longitude: String = "",              // 경도 (x 좌표)
    @SerialName("y") val latitude: String = "",
) {
    fun toData(): SubwayEntity {
        return SubwayEntity(title, distance, latitude, longitude)
    }
}