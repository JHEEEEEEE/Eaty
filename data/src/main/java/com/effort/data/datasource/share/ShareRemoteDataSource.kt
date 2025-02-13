package com.effort.data.datasource.share

interface ShareRemoteDataSource {

    suspend fun createKakaoShareLink(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): String
}