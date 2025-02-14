package com.effort.data.repositoryimpl.home.restaurant.detail.surrounding

import android.util.Log
import com.effort.data.datasource.home.restaurant.detail.surrounding.RestaurantSurroundingRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.subway.Subway
import com.effort.domain.model.home.restaurant.detail.weather.Weather
import com.effort.domain.repository.home.restaurant.detail.surrounding.RestaurantSurroundingRepository
import javax.inject.Inject

class RestaurantSurroundingRepositoryImpl @Inject constructor(
    private val restaurantSurroundingRemoteDataSource: RestaurantSurroundingRemoteDataSource
) : RestaurantSurroundingRepository {

    override suspend fun getWeatherData(
        latitude: String,
        longitude: String
    ): DataResource<List<Weather>> {

        DataResource.loading<List<Weather>>()

        return try {
            DataResource.success(
                restaurantSurroundingRemoteDataSource.getWeatherData(
                    latitude,
                    longitude
                ).map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("RestaurantDetailRepositoryImpl", "조회 실패: ${e.message}", e)
            DataResource.error(e)
        }
    }

    override suspend fun getSubwayStation(
        latitude: String,
        longitude: String
    ): DataResource<List<Subway>> {

        DataResource.loading<List<Subway>>()

        return try {
            DataResource.success(
                restaurantSurroundingRemoteDataSource.getSubwayStation(
                    latitude, longitude
                ).map { it.toDomain() }
            )
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}