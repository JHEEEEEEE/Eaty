package com.effort.data.model.home.restaurant

import com.effort.domain.model.home.restaurant.Restaurant

data class RestaurantEntity(
    val title: String,
    val lotNumberAddress: String,
    val roadNameAddress: String,
    val phoneNumber: String,
    val placeUrl: String,
    val distance: String,
    val longitude: String,
    val latitude: String,
) {
    fun toDomain(): Restaurant = Restaurant(
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

fun Restaurant.toData(): RestaurantEntity {
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
