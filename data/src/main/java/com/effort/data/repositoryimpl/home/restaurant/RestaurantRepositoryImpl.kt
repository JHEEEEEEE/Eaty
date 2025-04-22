package com.effort.data.repositoryimpl.home.restaurant

import com.effort.data.datasource.home.restaurant.RestaurantLocalDataSource
import com.effort.data.datasource.home.restaurant.RestaurantRemoteDataSource
import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.model.home.restaurant.RestaurantMeta
import com.effort.domain.repository.home.restaurant.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
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
    override fun getRestaurantList(
        query: String, page: Int
    ): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>> = flow {
        Timber.d("getRestaurantList() 호출됨 - query: $query, page: $page")

        emit(DataResource.loading())

        try {
            // 1. 로컬 데이터 조회
            val localRestaurants = restaurantLocalDataSource.getRestaurantList(query)
            if (localRestaurants.isNotEmpty()) {
                Timber.d("로컬 데이터 존재 - 개수: ${localRestaurants.size}")
                emit(DataResource.success(Pair(localRestaurants.map { it.toDomain() }, null)))
                return@flow
            }

            // 2. 원격 데이터 요청
            Timber.d("로컬 데이터 없음 - 원격 데이터 요청 시작")
            val (remoteRestaurants, meta) = restaurantRemoteDataSource.getRestaurants(query, page)

            // 3. 원격 데이터 로컬 저장
            Timber.d("원격 데이터 조회 완료 - 개수: ${remoteRestaurants.size}, 메타정보: $meta")
            restaurantLocalDataSource.insertRestaurantList(remoteRestaurants.map {
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
            })

            // 4. 최종 데이터 반환
            Timber.d("데이터 변환 후 반환 - 개수: ${remoteRestaurants.size}")
            emit(
                DataResource.success(
                    Pair(
                        remoteRestaurants.map { it.toDomain() }, meta.toDomain()
                    )
                )
            )

        } catch (e: Exception) {
            Timber.e(e, "getRestaurantList() 실패 - query: $query, page: $page")
            emit(DataResource.error(e))
        }
    }
}