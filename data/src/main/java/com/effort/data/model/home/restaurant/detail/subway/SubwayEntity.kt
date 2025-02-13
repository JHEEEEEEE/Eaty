package com.effort.data.model.home.restaurant.detail.subway

import com.effort.domain.model.home.restaurant.detail.subway.Subway

data class SubwayEntity(
    val placeName: String,
    val distance: String,
    val latitude: String,
    val longitude: String,
) {
    fun toDomain(): Subway {
        return Subway(placeName, distance, latitude, longitude)
    }
}