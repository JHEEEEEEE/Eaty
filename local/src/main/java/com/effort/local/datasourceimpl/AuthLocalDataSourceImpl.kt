package com.effort.local.datasourceimpl

import android.util.Log
import com.effort.data.datasource.auth.AuthLocalDataSource
import com.effort.local.dao.UserDao
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
): AuthLocalDataSource {

    override suspend fun clearUsers() {
        try {
            userDao.clearUsers() // 데이터 삭제
        } catch (e: Exception) {
            Log.e("AuthLocalDataSourceImpl", "Failed to clear local users: ${e.message}")
            throw e // 예외 전파
        }
    }
}