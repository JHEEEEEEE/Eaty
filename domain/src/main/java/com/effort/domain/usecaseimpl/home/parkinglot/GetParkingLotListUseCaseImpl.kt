package com.effort.domain.usecaseimpl.home.parkinglot

import com.effort.domain.DataResource
import com.effort.domain.model.home.parkinglot.ParkingLot
import com.effort.domain.repository.home.RestaurantDetailRepository
import com.effort.domain.usecase.home.parkinglot.GetParkingLotListUseCase
import javax.inject.Inject

class GetParkingLotListUseCaseImpl @Inject constructor(
    private val restaurantDetailRepository: RestaurantDetailRepository
): GetParkingLotListUseCase {

    override suspend fun invoke(latitude: String, longitude: String): DataResource<List<ParkingLot>> {
        return restaurantDetailRepository.getNearestParkingLots(latitude, longitude)
    }
}