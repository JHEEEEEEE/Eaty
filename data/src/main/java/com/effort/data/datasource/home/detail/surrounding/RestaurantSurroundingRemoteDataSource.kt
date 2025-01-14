package com.effort.data.datasource.home.detail.surrounding

import com.effort.data.model.home.weather.WeatherEntity

interface RestaurantSurroundingRemoteDataSource {

    suspend fun getWeatherData(latitude: String, longitude: String): List<WeatherEntity>
}