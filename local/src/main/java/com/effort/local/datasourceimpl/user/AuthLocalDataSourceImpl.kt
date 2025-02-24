package com.effort.local.datasourceimpl.user

import com.effort.data.datasource.auth.AuthLocalDataSource
import com.effort.local.dao.user.UserDao
import timber.log.Timber
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : AuthLocalDataSource {

    /**
     * 로컬 데이터베이스에서 모든 사용자 데이터를 삭제한다.
     * - 삭제 성공 시 로그 출력
     * - 실패 시 예외를 로깅하고 다시 던짐
     */
    override suspend fun clearUsers() {
        try {
            Timber.d("clearUsers() 호출 - 로컬 사용자 데이터 삭제 시도")
            userDao.clearUsers() // 데이터 삭제
            Timber.d("clearUsers() 완료 - 로컬 사용자 데이터 삭제 성공")
        } catch (e: Exception) {
            Timber.e(e, "clearUsers() 실패 - 로컬 사용자 데이터 삭제 중 오류 발생")
            throw e
        }
    }
}