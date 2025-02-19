package com.effort.data.repositoryimpl.home.restaurant.detail.review

import com.effort.data.datasource.home.restaurant.detail.review.RestaurantReviewRemoteDataSource
import com.effort.data.model.home.restaurant.detail.comment.toData
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.comment.Comment
import com.effort.domain.repository.home.restaurant.detail.review.RestaurantReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

class RestaurantReviewRepositoryImpl @Inject constructor(
    private val restaurantReviewRemoteDataSource: RestaurantReviewRemoteDataSource
) : RestaurantReviewRepository {

    override suspend fun addComment(restaurantId: String, comment: Comment): DataResource<Boolean> {
        Timber.d("addComment() 호출됨 - restaurantId: $restaurantId, comment: ${comment.content}")

        DataResource.loading<Boolean>()

        return try {
            val result = restaurantReviewRemoteDataSource.addComment(restaurantId, comment.toData())
            Timber.d("addComment() 성공 - restaurantId: $restaurantId, result: $result")
            DataResource.success(result)
        } catch (e: Exception) {
            Timber.e(e, "addComment() 실패 - restaurantId: $restaurantId")
            DataResource.error(e)
        }
    }

    override fun getComment(restaurantId: String): Flow<DataResource<List<Comment>>> = channelFlow {
        Timber.d("getComment() 호출됨 - restaurantId: $restaurantId")

        trySend(DataResource.loading())

        try {
            restaurantReviewRemoteDataSource.getComment(restaurantId)
                .collectLatest { commentEntities ->
                    Timber.d("getComment() 성공 - restaurantId: $restaurantId, 댓글 개수: ${commentEntities.size}")
                    trySend(DataResource.success(commentEntities.map { it.toDomain() }))
                }
        } catch (e: Exception) {
            Timber.e(e, "getComment() 실패 - restaurantId: $restaurantId")
            trySend(DataResource.error(e))
        }
    }
}