package com.effort.domain.usecaseimpl.home.restaurant.detail.parkinglot

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.parkinglot.ParkingLot
import com.effort.domain.repository.home.restaurant.detail.surrounding.RestaurantSurroundingRepository
import com.effort.domain.usecase.home.restaurant.detail.parkinglot.GetParkingLotListUseCase
import javax.inject.Inject

class GetParkingLotListUseCaseImpl @Inject constructor(
    private val restaurantSurroundingRepository: RestaurantSurroundingRepository
) : GetParkingLotListUseCase {

    override suspend fun invoke(
        latitude: String,
        longitude: String
    ): DataResource<List<ParkingLot>> {
        return restaurantSurroundingRepository.getNearestParkingLots(latitude, longitude)
    }
}