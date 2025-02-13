package com.effort.domain.usecase.home.restaurant.detail.subway

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.subway.Subway

interface GetSubwayStationUseCase {

    suspend operator fun invoke(latitude: String, longitude: String): DataResource<List<Subway>>
}