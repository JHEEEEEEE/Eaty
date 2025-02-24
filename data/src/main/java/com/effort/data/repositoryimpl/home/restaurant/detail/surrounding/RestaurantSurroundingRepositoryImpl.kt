package com.effort.data.repositoryimpl.home.restaurant.detail.surrounding

import com.effort.data.datasource.home.restaurant.detail.surrounding.RestaurantSurroundingRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.subway.Subway
import com.effort.domain.model.home.restaurant.detail.weather.Weather
import com.effort.domain.repository.home.restaurant.detail.surrounding.RestaurantSurroundingRepository
import timber.log.Timber
import javax.inject.Inject

class RestaurantSurroundingRepositoryImpl @Inject constructor(
    private val restaurantSurroundingRemoteDataSource: RestaurantSurroundingRemoteDataSource
) : RestaurantSurroundingRepository {

    override suspend fun getWeatherData(
        latitude: String, longitude: String
    ): DataResource<List<Weather>> {

        Timber.d("getWeatherData() 호출됨 - latitude: $latitude, longitude: $longitude")

        DataResource.loading<List<Weather>>()

        return try {
            val weatherList =
                restaurantSurroundingRemoteDataSource.getWeatherData(latitude, longitude)
                    .map { it.toDomain() }

            Timber.d("getWeatherData() 성공 - 데이터 개수: ${weatherList.size}")
            DataResource.success(weatherList)
        } catch (e: Exception) {
            Timber.e(e, "getWeatherData() 실패 - latitude: $latitude, longitude: $longitude")
            DataResource.error(e)
        }
    }

    override suspend fun getSubwayStation(
        latitude: String, longitude: String
    ): DataResource<List<Subway>> {

        Timber.d("getSubwayStation() 호출됨 - latitude: $latitude, longitude: $longitude")

        DataResource.loading<List<Subway>>()

        return try {
            val subwayList =
                restaurantSurroundingRemoteDataSource.getSubwayStation(latitude, longitude)
                    .map { it.toDomain() }

            Timber.d("getSubwayStation() 성공 - 데이터 개수: ${subwayList.size}")
            DataResource.success(subwayList)
        } catch (e: Exception) {
            Timber.e(e, "getSubwayStation() 실패 - latitude: $latitude, longitude: $longitude")
            DataResource.error(e)
        }
    }
}