package com.effort.data.repositoryimpl.share

import com.effort.data.datasource.share.ShareRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.share.ShareRepository
import javax.inject.Inject

class ShareRepositoryImpl @Inject constructor(
    private val shareRemoteDataSource: ShareRemoteDataSource
) : ShareRepository {

    override suspend fun createKakaoShareLink(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): DataResource<String> {
        DataResource.loading<Boolean>()

        return try {
            DataResource.success(
                shareRemoteDataSource.createKakaoShareLink(
                    title, lotNumberAddress, roadNameAddress, distance, phoneNumber, placeUrl
                )
            )
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}