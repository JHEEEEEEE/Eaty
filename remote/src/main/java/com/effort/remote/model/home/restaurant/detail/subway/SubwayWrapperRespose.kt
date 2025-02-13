package com.effort.remote.model.home.restaurant.detail.subway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubwayWrapperRespose(

    @SerialName("documents")
    val documents: List<SubwayResponse>
)