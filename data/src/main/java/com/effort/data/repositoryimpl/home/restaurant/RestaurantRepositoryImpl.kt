package com.effort.data.repositoryimpl.home.restaurant

import android.util.Log
import com.effort.data.datasource.home.restaurant.RestaurantLocalDataSource
import com.effort.data.datasource.home.restaurant.RestaurantRemoteDataSource
import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.model.home.restaurant.RestaurantMeta
import com.effort.domain.repository.home.restaurant.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantRemoteDataSource: RestaurantRemoteDataSource,
    private val restaurantLocalDataSource: RestaurantLocalDataSource
) : RestaurantRepository {

    override fun getRestaurantList(query: String, page: Int): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>> = flow {
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
                emit(DataResource.success(Pair(localRestaurants.map { it.toDomain() }, null)))
            } else {
                // 2. 원격 데이터 조회
                val (remoteRestaurants, meta) = restaurantRemoteDataSource.getRestaurants(query, page)

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
                            lotNumberAddress = it.lotNumberAddress,
                            roadNameAddress = it.roadNameAddress,
                            phoneNumber = it.phoneNumber,
                            placeUrl = it.placeUrl,
                            distance = it.distance,
                            latitude = it.latitude,
                            longitude = it.longitude,
                        )
                    }
                )

                // 4. 원격 데이터 반환
                emit(DataResource.success(Pair(remoteRestaurants.map { it.toDomain() }, meta.toDomain())))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}

