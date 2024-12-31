package com.effort.remote.service.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationService {

    suspend fun getCurrentLocation(): Location?

    fun getLocationUpdates(): Flow<Location>
}