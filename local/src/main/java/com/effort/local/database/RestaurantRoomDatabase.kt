package com.effort.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.effort.local.dao.RestaurantDao
import com.effort.local.model.RestaurantLocal

@Database(entities = [RestaurantLocal::class], version = 1, exportSchema = false)
abstract class RestaurantRoomDatabase: RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}