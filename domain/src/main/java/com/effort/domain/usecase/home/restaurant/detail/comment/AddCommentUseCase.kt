package com.effort.domain.usecase.home.restaurant.detail.comment

import com.effort.domain.DataResource
import com.effort.domain.model.home.comment.Comment

interface AddCommentUseCase {

    suspend operator fun invoke(restaurantId: String, comment: Comment): DataResource<Boolean>
}