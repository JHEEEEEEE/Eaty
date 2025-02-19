package com.effort.remote.model.home.restaurant

import com.effort.data.model.home.restaurant.RestaurantEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RestaurantResponse(

    @SerialName("place_name") val title: String = "",          // 장소명
    //@SerialName("category_name") val category: String = "",    // 카테고리
    @SerialName("address_name") val lotNumberAddress: String = "",      // 지번 주소
    @SerialName("road_address_name") val roadNameAddress: String = "", // 도로명 주소
    @SerialName("phone") val phoneNumber: String = "",
    @SerialName("place_url") val placeUrl: String = "",
    @SerialName("distance") val distance: String = "",
    @SerialName("x") val longitude: String = "",              // 경도 (x 좌표)
    @SerialName("y") val latitude: String = "",                 // 위도 (y 좌표)
) {
    // Data Layer의 RestaurantEntity로 변환
    fun toData(): RestaurantEntity {
        return RestaurantEntity(
            title,
            lotNumberAddress,
            roadNameAddress,
            phoneNumber,
            placeUrl,
            distance,
            longitude,
            latitude
        )
    }
}

fun RestaurantEntity.toRemote(): RestaurantResponse {
    return RestaurantResponse(
        title,
        lotNumberAddress,
        roadNameAddress,
        phoneNumber,
        placeUrl,
        distance,
        longitude,
        latitude
    )
}