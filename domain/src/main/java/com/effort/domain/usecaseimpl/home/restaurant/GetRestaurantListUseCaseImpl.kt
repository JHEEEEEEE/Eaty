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
    private val restaurantRepository: RestaurantRepository
) : GetRestaurantListUseCase {

    /**
     * 레스토랑 데이터를 가져와 정렬 후 반환한다.
     * - 기본 정렬(거리순)은 API에서 처리됨
     * - 추가적인 정렬 옵션(이름순 등)은 클라이언트에서 적용
     */
    override fun invoke(
        query: String,
        sortType: SortType,
        page: Int
    ): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>> = channelFlow {
        send(DataResource.loading()) // 로딩 상태 전송

        try {
            restaurantRepository.getRestaurantList(query, page)
                .collectLatest { restaurantResult ->
                    when (restaurantResult) {
                        is DataResource.Success -> {
                            val (restaurants, meta) = restaurantResult.data

                            // 클라이언트 단에서 추가 정렬 처리
                            val sortedRestaurants = sortRestaurants(restaurants, sortType)

                            send(DataResource.success(Pair(sortedRestaurants, meta)))
                        }

                        is DataResource.Error -> send(DataResource.error(restaurantResult.throwable))

                        is DataResource.Loading -> send(DataResource.loading())
                    }
                }
        } catch (e: Exception) {
            send(DataResource.error(e))
        }
    }

    /**
     * 정렬 처리한다.
     * - API에서 제공하는 거리순 정렬을 기본값으로 유지
     * - 클라이언트에서 추가 정렬 옵션(이름순) 적용 가능
     */
    private fun sortRestaurants(
        restaurants: List<Restaurant>,
        sortType: SortType
    ): List<Restaurant> {
        return when (sortType) {
            SortType.DEFAULT -> restaurants // 거리순 유지
            SortType.NAME -> restaurants.sortedBy { it.title } // 이름순 정렬
        }
    }
}