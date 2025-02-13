package com.effort.remote.datasourceimpl.share

import com.effort.data.datasource.share.ShareRemoteDataSource
import com.effort.remote.service.share.ShareService
import javax.inject.Inject

class ShareRemoteDataSourceImpl @Inject constructor(
    private val shareService: ShareService
) : ShareRemoteDataSource {

    override suspend fun createKakaoShareLink(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): String {
        return shareService.createKakaoShareUrl(title, lotNumberAddress, roadNameAddress, distance, phoneNumber, placeUrl)
    }
}