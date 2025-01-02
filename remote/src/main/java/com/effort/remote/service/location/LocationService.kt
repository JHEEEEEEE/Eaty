package com.effort.remote.service.location

import android.location.Location
import com.effort.remote.model.location.LocationResponse
import kotlinx.coroutines.flow.Flow

interface LocationService {

    suspend fun getCurrentLocation(): LocationResponse?

    //fun getLocationUpdates(): Flow<Location>
}