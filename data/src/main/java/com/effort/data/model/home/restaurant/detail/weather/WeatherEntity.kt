package com.effort.data.model.home.restaurant.detail.weather

import com.effort.domain.model.home.weather.Weather

data class WeatherEntity(
    val dateTime: String,
    val temp: Double,
    val condition: String,
    val iconUrl: String,
) {
    fun toDomain(): Weather {
        return Weather(dateTime, temp, condition, iconUrl)
    }
}
