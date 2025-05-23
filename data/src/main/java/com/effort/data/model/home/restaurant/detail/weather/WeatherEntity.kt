package com.effort.data.model.home.restaurant.detail.weather

import com.effort.domain.model.home.restaurant.detail.weather.Weather

data class WeatherEntity(
    val dateTime: String,
    val temp: Double,
    val condition: String,
    val id: Int,
) {
    fun toDomain(): Weather {
        return Weather(dateTime, temp, condition, id)
    }
}