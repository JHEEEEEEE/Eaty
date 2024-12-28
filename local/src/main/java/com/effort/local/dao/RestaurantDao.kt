package com.effort.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.effort.local.model.RestaurantLocal

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants WHERE title LIKE :query")
    suspend fun getRestaurants(query: String): List<RestaurantLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<RestaurantLocal>)
}