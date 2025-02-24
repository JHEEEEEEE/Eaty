package com.effort.data.repositoryimpl.share

import com.effort.data.datasource.share.ShareRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.repository.share.ShareRepository
import timber.log.Timber
import javax.inject.Inject

class ShareRepositoryImpl @Inject constructor(
    private val shareRemoteDataSource: ShareRemoteDataSource
) : ShareRepository {

    /**
     * 카카오 공유 링크를 생성한다.
     * - 원격 데이터 소스를 호출하여 공유 링크를 생성
     * - 성공하면 링크를 반환하고, 실패하면 예외를 로깅 후 에러 반환
     */
    override suspend fun createKakaoShareLink(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): DataResource<String> {
        Timber.d(
            "createKakaoShareLink() 호출: title=$title, lotNumberAddress=$lotNumberAddress, roadNameAddress=$roadNameAddress, distance=$distance, phoneNumber=$phoneNumber, placeUrl=$placeUrl"
        )

        DataResource.loading<Boolean>()

        return try {
            val shareLink = shareRemoteDataSource.createKakaoShareLink(
                title, lotNumberAddress, roadNameAddress, distance, phoneNumber, placeUrl
            )

            Timber.d("카카오 공유 링크 생성 성공: $shareLink")
            DataResource.success(shareLink)
        } catch (e: Exception) {
            Timber.e(e, "카카오 공유 링크 생성 실패")
            DataResource.error(e)
        }
    }
}