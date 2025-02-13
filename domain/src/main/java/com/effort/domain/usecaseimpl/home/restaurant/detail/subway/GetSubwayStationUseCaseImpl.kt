package com.effort.domain.usecaseimpl.home.restaurant.detail.subway

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.subway.Subway
import com.effort.domain.repository.home.restaurant.detail.surrounding.RestaurantSurroundingRepository
import com.effort.domain.usecase.home.restaurant.detail.subway.GetSubwayStationUseCase
import javax.inject.Inject

class GetSubwayStationUseCaseImpl @Inject constructor(
    private val restaurantSurroundingRepository: RestaurantSurroundingRepository
): GetSubwayStationUseCase {

    override suspend fun invoke(latitude: String, longitude: String): DataResource<List<Subway>> {
        return restaurantSurroundingRepository.getSubwayStation(latitude, longitude) // 나중에 여기서 정렬하면 됨. distance 순서로
    }
}