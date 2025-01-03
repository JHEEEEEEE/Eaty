package com.effort.data.repositoryimpl.home

import android.util.Log
import com.effort.data.datasource.home.RestaurantLocalDataSource
import com.effort.data.datasource.home.RestaurantRemoteDataSource
import com.effort.data.model.home.RestaurantEntity
import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import com.effort.domain.repository.home.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantRemoteDataSource: RestaurantRemoteDataSource,
    private val restaurantLocalDataSource: RestaurantLocalDataSource
) : RestaurantRepository {

    override fun getRestaurantList(query: String): Flow<DataResource<List<Restaurant>>> = flow {
        emit(DataResource.loading())
        try {
            // 1. 로컬 데이터 조회
            val localRestaurants = restaurantLocalDataSource.getRestaurantList(query)

            // **로컬 데이터의 위경도 로그 출력**
            localRestaurants.forEach { restaurant ->
                Log.d(
                    "RestaurantLog",
                    "로컬 레스토랑 - 이름: ${restaurant.title}, 위도: ${restaurant.latitude}, 경도: ${restaurant.longitude}"
                )
            }

            // 로컬 데이터 반환
            if (localRestaurants.isNotEmpty()) {
                emit(DataResource.success(localRestaurants.map { it.toDomain() }))
            } else {
                // 2. 원격 데이터 조회
                val remoteRestaurants = restaurantRemoteDataSource.getRestaurants(query).map { it.toDomain() }

                // **원격 데이터의 위경도 로그 출력**
                remoteRestaurants.forEach { restaurant ->
                    Log.d(
                        "RestaurantLog",
                        "원격 레스토랑 - 이름: ${restaurant.title}, 위도: ${restaurant.latitude}, 경도: ${restaurant.longitude}"
                    )
                }

                // 3. 원격 데이터 로컬 저장
                restaurantLocalDataSource.insertRestaurantList(
                    remoteRestaurants.map {
                        RestaurantEntity(
                            title = it.title,
                            latitude = it.latitude,
                            longitude = it.longitude,
                        )
                    }
                )

                // 4. 원격 데이터 반환
                emit(DataResource.success(remoteRestaurants))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}

