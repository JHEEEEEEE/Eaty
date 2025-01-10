package com.effort.domain.usecaseimpl.home.weather

import com.effort.domain.DataResource
import com.effort.domain.model.home.weather.Weather
import com.effort.domain.repository.home.RestaurantDetailRepository
import com.effort.domain.usecase.home.weather.GetWeatherDataUseCase
import javax.inject.Inject

class GetWeatherDataUseCaseImpl @Inject constructor(
    private val restaurantDetailRepository: RestaurantDetailRepository
): GetWeatherDataUseCase {

    override suspend fun invoke(
        latitude: String,
        longitude: String
    ): DataResource<List<Weather>> {
        return restaurantDetailRepository.getWeatherData(latitude, longitude)
    }
}