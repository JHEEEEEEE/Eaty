package com.effort.data.datasource.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRemoteDataSource {

    suspend fun getCurrentLocation(): Location?

    fun getLocationUpdates(): Flow<Location>
}