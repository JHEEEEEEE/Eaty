package com.effort.data.datasource.home.detail.review

import com.effort.data.model.home.comment.CommentEntity
import kotlinx.coroutines.flow.Flow

interface RestaurantReviewRemoteDataSource {

    suspend fun addComment(restaurantId: String, comment: CommentEntity): Boolean

    fun getComment(restaurantId: String): Flow<List<CommentEntity>>
}