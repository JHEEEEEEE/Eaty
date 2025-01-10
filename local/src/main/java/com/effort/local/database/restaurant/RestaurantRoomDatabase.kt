package com.effort.local.database.restaurant

import androidx.room.Database
import androidx.room.RoomDatabase
import com.effort.local.dao.restaurant.RestaurantDao
import com.effort.local.model.restaurant.RestaurantLocal

@Database(entities = [RestaurantLocal::class], version = 1, exportSchema = false)
abstract class RestaurantRoomDatabase: RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}