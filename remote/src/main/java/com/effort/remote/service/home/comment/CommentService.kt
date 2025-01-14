package com.effort.remote.service.home.comment

import com.effort.remote.model.home.comment.CommentResponse
import com.effort.remote.model.home.comment.CommentWrapperResponse
import kotlinx.coroutines.flow.Flow


interface CommentService {

    suspend fun addComment(restaurantId: String, comment: CommentResponse): Boolean

    fun getCommentList(restaurantId: String): Flow<CommentWrapperResponse>
}