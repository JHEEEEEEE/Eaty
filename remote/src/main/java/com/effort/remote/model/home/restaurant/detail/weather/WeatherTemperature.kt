package com.effort.remote.model.home.restaurant.detail.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherTemperature(

    @SerialName("temp") val temp: Double = 0.0 // 현재 온도 (섭씨)
)