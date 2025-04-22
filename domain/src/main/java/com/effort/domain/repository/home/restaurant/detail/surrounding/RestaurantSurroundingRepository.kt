package com.effort.domain.repository.home.restaurant.detail.surrounding

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.subway.Subway
import com.effort.domain.model.home.restaurant.detail.weather.Weather

interface RestaurantSurroundingRepository {

    suspend fun getWeatherData(latitude: String, longitude: String): DataResource<List<Weather>>

    suspend fun getSubwayStation(latitude: String, longitude: String): DataResource<List<Subway>>
}