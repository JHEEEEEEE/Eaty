package com.effort.domain.repository.home.restaurant.detail.surrounding

import com.effort.domain.DataResource
import com.effort.domain.model.home.parkinglot.ParkingLot
import com.effort.domain.model.home.weather.Weather

interface RestaurantSurroundingRepository {

    suspend fun getNearestParkingLots(
        latitude: String,
        longitude: String
    ): DataResource<List<ParkingLot>>

    suspend fun getWeatherData(latitude: String, longitude: String): DataResource<List<Weather>>
}