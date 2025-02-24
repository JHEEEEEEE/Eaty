package com.effort.remote.model.location

import com.effort.data.model.location.LocationEntity

data class LocationResponse(
    val latitude: Double,
    val longitude: Double,
) {
    fun toData(): LocationEntity {
        return LocationEntity(latitude, longitude)
    }
}