package com.effort.domain.usecaseimpl.share

import com.effort.domain.DataResource
import com.effort.domain.repository.share.ShareRepository
import com.effort.domain.usecase.share.ShareUseCase
import javax.inject.Inject

class ShareUseCaseImpl @Inject constructor(
    private val shareRepository: ShareRepository
) : ShareUseCase {

    override suspend fun invoke(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): DataResource<String> {
        return shareRepository.createKakaoShareLink(title, lotNumberAddress, roadNameAddress, distance, phoneNumber, placeUrl)
    }
}