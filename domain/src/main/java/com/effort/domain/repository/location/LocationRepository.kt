package com.effort.domain.repository.location

import com.effort.domain.DataResource
import com.effort.domain.model.location.Location

interface LocationRepository {

    suspend fun getCurrentLocation(): DataResource<Location>
}