package com.effort.local.datasourceimpl.user

import android.util.Log
import com.effort.data.datasource.mypage.MyPageLocalDataSource
import com.effort.data.model.auth.UserEntity
import com.effort.local.dao.user.UserDao
import com.effort.local.model.user.toLocal
import javax.inject.Inject

class MyPageLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
): MyPageLocalDataSource {

    // 특정 이메일로 데이터 조회
    override suspend fun getUser(): UserEntity? {
        return try {
            userDao.getUser()?.toData()

        } catch (e: Exception) {
            Log.e("AuthLocalDataSourceImpl", "Failed to fetch user data: ${e.message}")
            null // 오류 발생 시 null 반환
        }
    }

    override suspend fun saveUser(userEntity: UserEntity) {
        userDao.saveUser(userEntity.toLocal())
    }
}