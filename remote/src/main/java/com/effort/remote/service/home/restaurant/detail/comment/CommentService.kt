package com.effort.remote.service.home.restaurant.detail.comment

import com.effort.remote.model.home.restaurant.detail.comment.CommentResponse
import com.effort.remote.model.home.restaurant.detail.comment.CommentWrapperResponse
import kotlinx.coroutines.flow.Flow


interface CommentService {

    suspend fun addComment(restaurantId: String, comment: CommentResponse): Boolean

    fun getCommentList(restaurantId: String): Flow<CommentWrapperResponse>
}