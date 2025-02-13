package com.effort.presentation.model.home.restaurant.detail.weather

import com.effort.domain.model.home.restaurant.detail.weather.Weather

data class WeatherModel(
    val dateTime: String,
    val temp: Double,
    val condition: String,
    val iconUrl: String,
)

fun Weather.toPresentation(): WeatherModel {
    return WeatherModel(dateTime, temp, condition, iconUrl)
}