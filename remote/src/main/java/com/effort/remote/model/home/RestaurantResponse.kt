package com.effort.remote.model.home

import com.effort.data.model.home.RestaurantEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RestaurantResponse(
    @SerialName("place_name") val title: String = "",          // 장소명
    @SerialName("category_name") val category: String = "",    // 카테고리
    @SerialName("address_name") val address: String = "",      // 지번 주소
    @SerialName("road_address_name") val roadAddress: String = "", // 도로명 주소
    @SerialName("x") val longitude: String = "",              // 경도 (x 좌표)
    @SerialName("y") val latitude: String = "",                 // 위도 (y 좌표)
) {

    // Data Layer의 RestaurantEntity로 변환
    fun toData(): RestaurantEntity {
        return RestaurantEntity(
            title = title,
            longitude = longitude, // 경도
            latitude = latitude.toString()   // 위도
        )
    }
}
