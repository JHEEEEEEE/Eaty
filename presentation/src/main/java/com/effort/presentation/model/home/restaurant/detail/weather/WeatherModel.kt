package com.effort.presentation.model.home.restaurant.detail.weather

import com.effort.domain.model.home.restaurant.detail.weather.Weather
import com.effort.presentation.R

data class WeatherModel(
    val dateTime: String,
    val temp: Double,
    val condition: String,
    val id: Int,
    val weatherIcon: Int = R.drawable.ic_cloudy  // 기본값 추가
)

fun Weather.toPresentation(): WeatherModel {
    return WeatherModel(dateTime, temp, condition, id)
}