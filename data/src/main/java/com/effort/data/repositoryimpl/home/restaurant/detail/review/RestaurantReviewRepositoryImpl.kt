package com.effort.data.repositoryimpl.home.restaurant.detail.review

import com.effort.data.datasource.home.restaurant.detail.review.RestaurantReviewRemoteDataSource
import com.effort.data.model.home.restaurant.detail.comment.toData
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.comment.Comment
import com.effort.domain.repository.home.restaurant.detail.review.RestaurantReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RestaurantReviewRepositoryImpl @Inject constructor(
    private val restaurantReviewRemoteDataSource: RestaurantReviewRemoteDataSource
) : RestaurantReviewRepository {

    override suspend fun addComment(restaurantId: String, comment: Comment): DataResource<Boolean> {
        DataResource.loading<Boolean>()

        return try {
            DataResource.success(
                restaurantReviewRemoteDataSource.addComment(
                    restaurantId,
                    comment.toData()
                )
            )
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override fun getComment(restaurantId: String): Flow<DataResource<List<Comment>>> = channelFlow {
        trySend(DataResource.loading())

        try {
            restaurantReviewRemoteDataSource.getComment(restaurantId)
                .collectLatest { commentEntities ->
                    trySend(DataResource.success(commentEntities.map { it.toDomain() }))
                }
        } catch (e: Exception) {
            trySend(DataResource.error(e))
        }
    }
}