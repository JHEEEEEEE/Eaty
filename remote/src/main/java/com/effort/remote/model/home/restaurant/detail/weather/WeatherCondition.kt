package com.effort.remote.model.home.restaurant.detail.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(

    @SerialName("id") val id: Int = 0,

    @SerialName("description") val condition: String = "",
)