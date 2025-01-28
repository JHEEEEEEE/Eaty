package com.effort.remote.model.home.restaurant.favorites

import com.effort.data.model.home.restaurant.RestaurantEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantFavoritesResponse(
    @SerialName("title")
    val title: String = "",

    @SerialName("lotNumberAddress")
    val lotNumberAddress: String = "",

    @SerialName("roadNameAddress")
    val roadNameAddress: String = "",

    @SerialName("phoneNumber")
    val phoneNumber: String = "",

    @SerialName("placeUrl")
    val placeUrl: String = "",

    @SerialName("distance")
    val distance: String = "",

    @SerialName("longitude")
    val longitude: String = "",

    @SerialName("latitude")
    val latitude: String = ""
) {
    fun toData(): RestaurantEntity {
        return RestaurantEntity(title, lotNumberAddress, roadNameAddress, phoneNumber, placeUrl, distance, longitude, latitude)
    }
}