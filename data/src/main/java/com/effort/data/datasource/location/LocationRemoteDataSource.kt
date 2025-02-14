package com.effort.data.datasource.location

import com.effort.data.model.location.LocationEntity

interface LocationRemoteDataSource {

    suspend fun getCurrentLocation(): LocationEntity
}