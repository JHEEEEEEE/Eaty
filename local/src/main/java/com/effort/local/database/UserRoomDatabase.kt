package com.effort.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.effort.local.dao.UserDao
import com.effort.local.model.UserLocal

@Database(entities = [UserLocal::class], version = 1, exportSchema = false)
abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}