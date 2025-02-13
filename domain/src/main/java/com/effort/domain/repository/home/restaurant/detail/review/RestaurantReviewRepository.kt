package com.effort.domain.repository.home.restaurant.detail.review

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.comment.Comment
import kotlinx.coroutines.flow.Flow

interface RestaurantReviewRepository {

    suspend fun addComment(restaurantId: String, comment: Comment): DataResource<Boolean>

    fun getComment(restaurantId: String): Flow<DataResource<List<Comment>>>
}