package com.effort.data.model.location

import com.effort.domain.model.location.Location

data class LocationEntity(
    val latitude: Double,
    val longitude: Double,
) {
    fun toDomain(): Location =
        Location(
            latitude = this.latitude,
            longitude = this.longitude
        )
}

