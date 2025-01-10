package com.effort.remote.model.home.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(

    @SerialName("main")
    val condition: String = "",

    @SerialName("icon")
    val icon: String = ""
)
