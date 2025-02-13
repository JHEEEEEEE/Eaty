package com.effort.presentation.model.home.restaurant.detail.subway

import com.effort.domain.model.home.restaurant.detail.subway.Subway

data class SubwayModel(
    val placeName: String,
    val distance: String,
    val latitude: String,
    val longitude: String,
)

fun Subway.toPresentation(): SubwayModel {
    return SubwayModel(placeName, distance, latitude, longitude)
}