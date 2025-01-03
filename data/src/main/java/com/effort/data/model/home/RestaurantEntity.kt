package com.effort.data.model.home

import com.effort.domain.model.home.Restaurant

data class RestaurantEntity(
    val title: String,
    val longitude: String,
    val latitude: String,
) {
    fun toDomain(): Restaurant =
        Restaurant(
            title = this.title,
            longitude = this.longitude, // 경도
            latitude = this.latitude
        )
}
