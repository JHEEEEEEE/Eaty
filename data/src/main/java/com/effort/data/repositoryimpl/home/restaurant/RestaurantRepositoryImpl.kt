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

    /**
     * 레스토랑 리스트를 가져온다. (로컬 데이터 우선, 없으면 원격 데이터 요청)
     * - 로컬 데이터가 존재하면 즉시 반환 후 종료
     * - 로컬 데이터가 없을 경우 원격 데이터 요청 후 로컬 저장
     */
    override fun getRestaurantList(query: String, page: Int): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>> = flow {
        emit(DataResource.loading())

        try {
            val localRestaurants = restaurantLocalDataSource.getRestaurantList(query)

            if (localRestaurants.isNotEmpty()) {
                emit(DataResource.success(Pair(localRestaurants.map { it.toDomain() }, null)))
                return@flow
            }

            val (remoteRestaurants, meta) = restaurantRemoteDataSource.getRestaurants(query, page)

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
                        longitude = it.longitude
                    )
                }
            )

            emit(DataResource.success(Pair(remoteRestaurants.map { it.toDomain() }, meta.toDomain())))

        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}