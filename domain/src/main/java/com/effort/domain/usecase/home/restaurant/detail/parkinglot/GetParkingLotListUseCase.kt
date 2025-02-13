package com.effort.domain.usecase.home.restaurant.detail.parkinglot

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.parkinglot.ParkingLot

interface GetParkingLotListUseCase {

    suspend operator fun invoke(latitude: String, longitude: String): DataResource<List<ParkingLot>>
}