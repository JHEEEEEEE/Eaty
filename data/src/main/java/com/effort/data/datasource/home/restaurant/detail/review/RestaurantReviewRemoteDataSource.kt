package com.effort.data.datasource.home.restaurant.detail.review

import com.effort.data.model.home.restaurant.detail.comment.CommentEntity
import kotlinx.coroutines.flow.Flow

interface RestaurantReviewRemoteDataSource {

    suspend fun addComment(restaurantId: String, comment: CommentEntity): Boolean

    fun getComment(restaurantId: String): Flow<List<CommentEntity>>
}