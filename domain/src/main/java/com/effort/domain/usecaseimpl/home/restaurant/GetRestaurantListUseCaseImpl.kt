package com.effort.domain.usecaseimpl.home.restaurant

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.model.home.restaurant.RestaurantMeta
import com.effort.domain.model.home.restaurant.SortType
import com.effort.domain.repository.home.restaurant.RestaurantRepository
import com.effort.domain.usecase.home.restaurant.GetRestaurantListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetRestaurantListUseCaseImpl @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
) : GetRestaurantListUseCase {

    override fun invoke(
        query: String,
        sortType: SortType,
        page: Int
    ): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>> = channelFlow {
        send(DataResource.loading()) // 로딩 상태 전송

        try {
            // 1. 레스토랑 리스트 가져오기 (거리순 정렬은 API 내부 처리)
            restaurantRepository.getRestaurantList(query, page)
                .collectLatest { restaurantResult ->
                    when (restaurantResult) {
                        is DataResource.Success -> {
                            val (restaurants, meta) = restaurantResult.data // 데이터와 메타 분리

                            // 추가 정렬 처리
                            val sortedRestaurants = sortRestaurants(
                                restaurants, sortType
                            )

                            // Meta 정보와 함께 전송
                            send(DataResource.success(Pair(sortedRestaurants, meta)))
                        }

                        is DataResource.Error -> {
                            send(DataResource.error(restaurantResult.throwable))
                        }

                        is DataResource.Loading -> {
                            send(DataResource.loading())
                        }
                    }
                }
        } catch (e: Exception) {
            send(DataResource.error(e))
        }
    }

    // 정렬 처리 메서드
    private fun sortRestaurants(
        restaurants: List<Restaurant>,
        sortType: SortType
    ): List<Restaurant> {
        return when (sortType) {
            SortType.DEFAULT -> restaurants // 거리순 유지 (기본값)
            SortType.NAME -> restaurants.sortedBy { it.title } // 이름순 정렬
        }
    }
}