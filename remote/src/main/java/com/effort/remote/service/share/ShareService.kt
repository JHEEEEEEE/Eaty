package com.effort.remote.service.share

interface ShareService {

    suspend fun createKakaoShareUrl(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): String
}