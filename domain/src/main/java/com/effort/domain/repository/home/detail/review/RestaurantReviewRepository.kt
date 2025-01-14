package com.effort.domain.repository.home.detail.review

import com.effort.domain.DataResource
import com.effort.domain.model.home.comment.Comment
import kotlinx.coroutines.flow.Flow

interface RestaurantReviewRepository {

    suspend fun addComment(restaurantId: String, comment: Comment): DataResource<Boolean>

    fun getComment(restaurantId: String): Flow<DataResource<List<Comment>>>
}