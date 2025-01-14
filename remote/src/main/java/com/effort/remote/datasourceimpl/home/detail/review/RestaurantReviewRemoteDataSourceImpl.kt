package com.effort.remote.datasourceimpl.home.detail.review

import com.effort.data.datasource.home.detail.review.RestaurantReviewRemoteDataSource
import com.effort.data.model.home.comment.CommentEntity
import com.effort.remote.model.home.comment.toRemote
import com.effort.remote.service.home.comment.CommentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RestaurantReviewRemoteDataSourceImpl @Inject constructor(
    private val commentService: CommentService
) : RestaurantReviewRemoteDataSource {

    override suspend fun addComment(restaurantId: String, comment: CommentEntity): Boolean {
        return commentService.addComment(restaurantId, comment.toRemote())
    }

    override fun getComment(restaurantId: String): Flow<List<CommentEntity>> {
        return commentService.getCommentList(restaurantId).map { wrapperResponse ->
            wrapperResponse.resultComments.map { it.toData() }
        }
    }
}