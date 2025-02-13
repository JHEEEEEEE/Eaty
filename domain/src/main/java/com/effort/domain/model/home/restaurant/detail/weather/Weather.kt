package com.effort.domain.model.home.restaurant.detail.weather

data class Weather(
    val dateTime: String,
    val temp: Double,
    val condition: String,
    val iconUrl: String,
)