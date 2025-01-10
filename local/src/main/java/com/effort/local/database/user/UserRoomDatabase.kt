package com.effort.local.database.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.effort.local.dao.user.UserDao
import com.effort.local.model.user.UserLocal

@Database(entities = [UserLocal::class], version = 1, exportSchema = false)
abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}