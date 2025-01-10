package com.effort.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.effort.local.model.user.UserLocal

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserLocal)

    @Query("DELETE FROM user")
    suspend fun clearUsers()
}