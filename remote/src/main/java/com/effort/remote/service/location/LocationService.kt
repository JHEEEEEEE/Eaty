package com.effort.remote.service.location

import com.effort.remote.model.location.LocationResponse

interface LocationService {

    suspend fun getCurrentLocation(): LocationResponse?
}