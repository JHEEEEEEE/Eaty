package com.effort.domain.usecaseimpl.home.weather

import com.effort.domain.DataResource
import com.effort.domain.model.home.weather.Weather
import com.effort.domain.repository.home.detail.surrounding.RestaurantSurroundingRepository
import com.effort.domain.usecase.home.restaurant.detail.weather.GetWeatherDataUseCase
import javax.inject.Inject

class GetWeatherDataUseCaseImpl @Inject constructor(
    private val restaurantSurroundingRepository: RestaurantSurroundingRepository
) : GetWeatherDataUseCase {

    override suspend fun invoke(
        latitude: String,
        longitude: String
    ): DataResource<List<Weather>> {
        return restaurantSurroundingRepository.getWeatherData(latitude, longitude)
    }
}