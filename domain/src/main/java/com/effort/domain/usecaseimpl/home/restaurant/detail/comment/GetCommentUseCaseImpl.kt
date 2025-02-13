package com.effort.domain.usecaseimpl.home.restaurant.detail.comment

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.comment.Comment
import com.effort.domain.repository.home.restaurant.detail.review.RestaurantReviewRepository
import com.effort.domain.usecase.home.restaurant.detail.comment.GetCommentUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentUseCaseImpl @Inject constructor(
    private val restaurantReviewRepository: RestaurantReviewRepository
) : GetCommentUseCase {

    override fun invoke(restaurantId: String): Flow<DataResource<List<Comment>>> {
        return restaurantReviewRepository.getComment(restaurantId)
    }
}