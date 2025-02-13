package com.effort.domain.usecase.home.restaurant.detail.comment

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.comment.Comment
import kotlinx.coroutines.flow.Flow

interface GetCommentUseCase {

    operator fun invoke(restaurantId: String): Flow<DataResource<List<Comment>>>
}