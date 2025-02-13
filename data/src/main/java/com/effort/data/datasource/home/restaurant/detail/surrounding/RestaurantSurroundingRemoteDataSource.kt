package com.effort.data.datasource.home.restaurant.detail.surrounding

import com.effort.data.model.home.restaurant.detail.subway.SubwayEntity
import com.effort.data.model.home.restaurant.detail.weather.WeatherEntity

interface RestaurantSurroundingRemoteDataSource {

    suspend fun getWeatherData(latitude: String, longitude: String): List<WeatherEntity>

    suspend fun getSubwayStation(latitude: String, longitude: String): List<SubwayEntity>
}