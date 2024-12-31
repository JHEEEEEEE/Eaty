package com.effort.data.repositoryimpl.home

import android.location.Location
import android.util.Log
import com.effort.data.core.util.LocationUtil
import com.effort.data.datasource.home.RestaurantLocalDataSource
import com.effort.data.datasource.home.RestaurantRemoteDataSource
import com.effort.data.datasource.location.LocationRemoteDataSource
import com.effort.data.model.home.RestaurantEntity
import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import com.effort.domain.model.home.SortType
import com.effort.domain.repository.home.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantRemoteDataSource: RestaurantRemoteDataSource,
    private val restaurantLocalDataSource: RestaurantLocalDataSource,
    private val locationRemoteDataSource: LocationRemoteDataSource
) : RestaurantRepository {

    override fun getRestaurantList(query: String, sortType: SortType): Flow<DataResource<List<Restaurant>>> = flow {
        Log.d("RestaurantRepositoryImpl", "getRestaurantList() 호출됨 - query: $query, sortType: $sortType")
        emit(DataResource.loading())

        try {
            // 1. 현재 위치 가져오기
            Log.d("RestaurantRepositoryImpl", "현재 위치 가져오기 시작")
            val currentLocation = locationRemoteDataSource.getCurrentLocation()
            if (currentLocation != null) {
                Log.d("RestaurantRepositoryImpl", "현재 위치 가져오기 성공: $currentLocation")
            } else {
                Log.e("RestaurantRepositoryImpl", "현재 위치 가져오기 실패")
            }

            // 2. 로컬 데이터 조회
            Log.d("RestaurantRepositoryImpl", "로컬 데이터 조회 시작 - query: $query")
            val localRestaurants = restaurantLocalDataSource.getRestaurantList(query)
            Log.d("RestaurantRepositoryImpl", "로컬 데이터 조회 완료: ${localRestaurants.size}개 항목")

            if (localRestaurants.isNotEmpty()) {
                Log.d("RestaurantRepositoryImpl", "로컬 데이터가 존재하므로 정렬 시작")
                val sortedLocalRestaurants = sortRestaurants(localRestaurants.map { it.toDomain() }, currentLocation, sortType)
                Log.d("RestaurantRepositoryImpl", "로컬 데이터 정렬 완료: ${sortedLocalRestaurants.size}개 항목")
                emit(DataResource.success(sortedLocalRestaurants))
            } else {
                // 3. 원격 데이터 조회
                Log.d("RestaurantRepositoryImpl", "원격 데이터 조회 시작 - query: $query")
                val remoteRestaurants = restaurantRemoteDataSource.getRestaurants(query).map { it.toDomain() }
                Log.d("RestaurantRepositoryImpl", "원격 데이터 조회 완료: ${remoteRestaurants.size}개 항목")

                // 4. 원격 데이터 로컬에 저장
                Log.d("RestaurantRepositoryImpl", "원격 데이터 로컬 저장 시작")
                restaurantLocalDataSource.insertRestaurantList(
                    remoteRestaurants.map {
                        RestaurantEntity(
                            title = it.title,
                            mapx = it.mapx,
                            mapy = it.mapy,
                        )
                    }
                )
                Log.d("RestaurantRepositoryImpl", "원격 데이터 로컬 저장 완료")

                // 정렬 및 반환
                Log.d("RestaurantRepositoryImpl", "원격 데이터 정렬 시작")
                val sortedRemoteRestaurants = sortRestaurants(remoteRestaurants, currentLocation, sortType)
                Log.d("RestaurantRepositoryImpl", "원격 데이터 정렬 완료: ${sortedRemoteRestaurants.size}개 항목")
                emit(DataResource.success(sortedRemoteRestaurants))
            }
        } catch (e: Exception) {
            Log.e("RestaurantRepositoryImpl", "데이터 조회 실패: ${e.message}", e)
            emit(DataResource.error(e))
        }
    }

    // 레스토랑 정렬 함수
    private fun sortRestaurants(
        restaurants: List<Restaurant>,
        currentLocation: Location?,
        sortType: SortType
    ): List<Restaurant> {
        Log.d("RestaurantRepositoryImpl", "레스토랑 정렬 시작 - sortType: $sortType")
        return when (sortType) {
            SortType.DISTANCE -> {
                currentLocation?.let { location ->
                    Log.d("RestaurantRepositoryImpl", "거리 기준 정렬 수행 - 현재 위치: $location")
                    restaurants.sortedBy {
                        val distance = LocationUtil.calculateDistance(
                            location.latitude,
                            location.longitude,
                            it.mapx.toDouble(),
                            it.mapy.toDouble(),
                        )
                        Log.d("RestaurantRepositoryImpl", "거리 계산 완료 - ${it.title}: $distance m")
                        distance
                    }
                } ?: restaurants.also {
                    Log.e("RestaurantRepositoryImpl", "정렬 실패 - 현재 위치 정보 없음")
                }
            }
        }
    }
}