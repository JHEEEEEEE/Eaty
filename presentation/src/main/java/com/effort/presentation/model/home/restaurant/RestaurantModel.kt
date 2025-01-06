package com.effort.presentation.model.home.restaurant

import com.effort.domain.model.home.restaurant.Restaurant


data class RestaurantModel(
    val title: String,
    val lotNumberAddress: String,
    val roadNameAddress: String,
    val phoneNumber: String,
    val placeUrl: String,
    val distance: String,
    val longitude: String,
    val latitude: String,
)

fun Restaurant.toPresentation(): RestaurantModel {
    return RestaurantModel(
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
