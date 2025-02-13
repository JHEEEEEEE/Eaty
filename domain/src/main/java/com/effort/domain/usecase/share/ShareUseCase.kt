package com.effort.domain.usecase.share

import com.effort.domain.DataResource

interface ShareUseCase {

    suspend operator fun invoke(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ): DataResource<String>
}