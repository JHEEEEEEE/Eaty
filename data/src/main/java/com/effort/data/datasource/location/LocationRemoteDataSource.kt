package com.effort.data.datasource.location

import android.location.Location
import com.effort.data.model.location.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationRemoteDataSource {

    suspend fun getCurrentLocation(): LocationEntity

    //fun getLocationUpdates(): Flow<Location>
}