package com.effort.domain.model.home.weather

data class Weather(
    val dateTime: String,
    val temp: Double,
    val condition: String,
    val iconUrl: String,
)
