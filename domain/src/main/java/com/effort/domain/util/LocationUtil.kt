package com.effort.domain.util

import com.effort.domain.model.home.Restaurant
import com.effort.domain.model.home.SortType
import com.effort.domain.model.location.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationUtil {

    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // Km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun sortRestaurants(
        restaurants: List<Restaurant>,
        sortType: SortType,
        currentLocation: Location
    ): List<Restaurant> {
        return when (sortType) {
            SortType.DISTANCE -> restaurants.sortedBy {
                LocationUtil.calculateDistance(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    it.mapx.toDouble(),
                    it.mapy.toDouble()
                )
            }

            SortType.NAME -> restaurants.sortedBy { it.title }
        }
    }
}