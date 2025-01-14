package com.effort.remote.model.home.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentWrapperResponse(

    @SerialName("comment_results")
    val resultComments: List<CommentResponse>
)