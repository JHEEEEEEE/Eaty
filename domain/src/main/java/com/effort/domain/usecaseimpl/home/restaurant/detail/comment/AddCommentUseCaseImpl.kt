package com.effort.domain.usecaseimpl.home.restaurant.detail.comment

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.comment.Comment
import com.effort.domain.repository.home.restaurant.detail.review.RestaurantReviewRepository
import com.effort.domain.usecase.home.restaurant.detail.comment.AddCommentUseCase
import javax.inject.Inject

class AddCommentUseCaseImpl @Inject constructor(
    private val restaurantReviewRepository: RestaurantReviewRepository
) : AddCommentUseCase {

    override suspend fun invoke(restaurantId: String, comment: Comment): DataResource<Boolean> {
        return restaurantReviewRepository.addComment(restaurantId, comment)
    }
}