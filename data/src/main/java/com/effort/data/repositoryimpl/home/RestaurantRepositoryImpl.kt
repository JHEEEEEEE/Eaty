package com.effort.data.repositoryimpl.home

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
): RestaurantRepository {

    override fun getRestaurantList(query: String): Flow<DataResource<List<Restaurant>>> = flow {
        emit(DataResource.loading())

        try {
            // 1. 먼저 로컬 데이터 조회
            val localRestaurants = restaurantLocalDataSource.getRestaurantList(query)
            if (localRestaurants.isNotEmpty()) {
                emit(DataResource.success(localRestaurants.map { it.toDomain() }))
            } else {
                // 2. 로컬에 없으면 원격 데이터 조회
                val remoteRestaurants = restaurantRemoteDataSource.getRestaurants(query).map { it.toDomain() }

                // 3. 원격 데이터 로컬에 저장
                restaurantLocalDataSource.insertRestaurantList(
                    remoteRestaurants.map {
                        RestaurantEntity(
                            title = it.title,
                        )
                    }
                )
                emit(DataResource.success(remoteRestaurants))
            }
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}