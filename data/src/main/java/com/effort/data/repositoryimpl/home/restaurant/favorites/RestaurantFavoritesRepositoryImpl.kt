package com.effort.data.repositoryimpl.home.restaurant.favorites

import com.effort.data.datasource.home.restaurant.favorites.RestaurantFavoritesRemoteDataSource
import com.effort.data.model.home.restaurant.toData
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.repository.home.restaurant.favorites.RestaurantFavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class RestaurantFavoritesRepositoryImpl @Inject constructor(
    private val restaurantFavoritesRemoteDataSource: RestaurantFavoritesRemoteDataSource
) : RestaurantFavoritesRepository {

    override suspend fun addRestaurantToFavorites(
        userId: String, restaurant: Restaurant
    ): DataResource<Boolean> {
        Timber.d("addRestaurantToFavorites() 호출됨 - userId: $userId, restaurant: ${restaurant.title}")

        return try {
            DataResource.loading<Boolean>()

            val result = restaurantFavoritesRemoteDataSource.addRestaurantToFavorites(
                userId, restaurant.toData()
            )
            Timber.d("addRestaurantToFavorites() 성공 - userId: $userId, restaurant: ${restaurant.title}, 결과: $result")

            DataResource.success(result)
        } catch (e: Exception) {
            Timber.e(
                e,
                "addRestaurantToFavorites() 실패 - userId: $userId, restaurant: ${restaurant.title}"
            )
            DataResource.error(e)
        }
    }

    override suspend fun removeRestaurantFromFavorites(
        userId: String, restaurantName: String
    ): DataResource<Boolean> {
        Timber.d("removeRestaurantFromFavorites() 호출됨 - userId: $userId, restaurantName: $restaurantName")

        return try {
            DataResource.loading<Boolean>()

            val result = restaurantFavoritesRemoteDataSource.removeRestaurantFromFavorites(
                userId, restaurantName
            )
            Timber.d("removeRestaurantFromFavorites() 성공 - userId: $userId, restaurantName: $restaurantName, 결과: $result")

            DataResource.success(result)
        } catch (e: Exception) {
            Timber.e(
                e,
                "removeRestaurantFromFavorites() 실패 - userId: $userId, restaurantName: $restaurantName"
            )
            DataResource.error(e)
        }
    }

    override suspend fun checkIfRestaurantIsFavorite(
        userId: String, restaurantName: String
    ): DataResource<Boolean> {
        Timber.d("checkIfRestaurantIsFavorite() 호출됨 - userId: $userId, restaurantName: $restaurantName")

        return try {
            DataResource.loading<Boolean>()

            val result = restaurantFavoritesRemoteDataSource.checkIfRestaurantIsFavorite(
                userId, restaurantName
            )
            Timber.d("checkIfRestaurantIsFavorite() 성공 - userId: $userId, restaurantName: $restaurantName, 결과: $result")

            DataResource.success(result)
        } catch (e: Exception) {
            Timber.e(
                e,
                "checkIfRestaurantIsFavorite() 실패 - userId: $userId, restaurantName: $restaurantName"
            )
            DataResource.error(e)
        }
    }

    override fun getFavorites(userId: String): Flow<DataResource<List<Restaurant>>> = flow {
        Timber.d("getFavorites() 호출됨 - userId: $userId")

        emit(DataResource.loading())

        try {
            val favorites =
                restaurantFavoritesRemoteDataSource.getFavorites(userId).map { it.toDomain() }
            Timber.d("getFavorites() 성공 - userId: $userId, 즐겨찾기 개수: ${favorites.size}")

            emit(DataResource.success(favorites))
        } catch (e: Exception) {
            Timber.e(e, "getFavorites() 실패 - userId: $userId")
            emit(DataResource.error(e))
        }
    }
}