package com.effort.domain.usecaseimpl.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import com.effort.domain.model.home.SortType
import com.effort.domain.model.location.Location
import com.effort.domain.repository.home.RestaurantRepository
import com.effort.domain.repository.location.LocationRepository
import com.effort.domain.usecase.home.GetRestaurantListUseCase
import com.effort.domain.util.LocationUtil
import com.effort.domain.util.LocationUtil.sortRestaurants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class GetRestaurantListUseCaseImpl @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val locationRepository: LocationRepository
) : GetRestaurantListUseCase {

    override fun invoke(query: String, sortType: SortType): Flow<DataResource<List<Restaurant>>> =
        channelFlow { // channelFlow 사용으로 병렬 emit 허용
            send(DataResource.loading()) // emit 대신 send 사용

            try {
                // 1. 현재 위치 가져오기
                val locationResult = locationRepository.getCurrentLocation()
                if (locationResult is DataResource.Error) {
                    send(DataResource.error(locationResult.throwable))
                    return@channelFlow
                }

                val currentLocation = (locationResult as DataResource.Success).data // 위치 데이터 추출

                // 2. 레스토랑 리스트 가져오기
                restaurantRepository.getRestaurantList(query) // Flow 수집
                    .collectLatest { restaurantResult ->
                        when (restaurantResult) {
                            is DataResource.Success -> {
                                // 3. 정렬 처리
                                val sortedRestaurants = sortRestaurants(
                                    restaurantResult.data,
                                    sortType,
                                    currentLocation
                                )
                                send(DataResource.success(sortedRestaurants)) // send 사용
                            }

                            is DataResource.Error -> {
                                send(DataResource.error(restaurantResult.throwable)) // send 사용
                            }

                            is DataResource.Loading -> {
                                send(DataResource.loading()) // send 사용
                            }
                        }
                    }
            } catch (e: Exception) {
                send(DataResource.error(e)) // send 사용
            }
        }

}