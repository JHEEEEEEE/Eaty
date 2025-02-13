package com.effort.domain.usecase.home.restaurant.detail.weather

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.weather.Weather

interface GetWeatherDataUseCase {

    suspend operator fun invoke(latitude: String, longitude: String): DataResource<List<Weather>>
}