package com.effort.data.repositoryimpl.home.restaurant.favorites

import com.effort.data.datasource.home.restaurant.favorites.RestaurantFavoritesRemoteDataSource
import com.effort.data.model.home.restaurant.toData
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.repository.home.restaurant.favorites.RestaurantFavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RestaurantFavoritesRepositoryImpl @Inject constructor(
    private val restaurantFavoritesRemoteDataSource: RestaurantFavoritesRemoteDataSource
): RestaurantFavoritesRepository {

    override suspend fun addRestaurantToFavorites(userId: String, restaurant: Restaurant): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()

            DataResource.success(restaurantFavoritesRemoteDataSource.addRestaurantToFavorites(userId, restaurant.toData()))
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override suspend fun removeRestaurantFromFavorites(
        userId: String,
        restaurantName: String
    ): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()

            DataResource.success(restaurantFavoritesRemoteDataSource.removeRestaurantFromFavorites(userId, restaurantName))
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override suspend fun checkIfRestaurantIsFavorite(
        userId: String,
        restaurantName: String
    ): DataResource<Boolean> {
        return try {
            DataResource.loading<Boolean>()

            DataResource.success(restaurantFavoritesRemoteDataSource.checkIfRestaurantIsFavorite(userId, restaurantName))
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override fun getFavorites(userId: String): Flow<DataResource<List<Restaurant>>> = flow {
        emit(DataResource.loading())

        try {
            val favorites = restaurantFavoritesRemoteDataSource.getFavorites(userId).map { it.toDomain() }
            emit(DataResource.success(favorites))
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}