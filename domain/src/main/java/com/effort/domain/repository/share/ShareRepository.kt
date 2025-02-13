package com.effort.domain.repository.share

import com.effort.domain.DataResource

interface ShareRepository {

    suspend fun createKakaoShareLink(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): DataResource<String>
}