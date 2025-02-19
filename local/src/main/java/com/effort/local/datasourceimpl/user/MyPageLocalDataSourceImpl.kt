package com.effort.local.datasourceimpl.user

import com.effort.data.datasource.mypage.MyPageLocalDataSource
import com.effort.data.model.auth.UserEntity
import com.effort.local.dao.user.UserDao
import com.effort.local.model.user.toLocal
import timber.log.Timber
import javax.inject.Inject

class MyPageLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : MyPageLocalDataSource {

    /**
     * 특정 이메일을 기반으로 사용자 정보를 조회한다.
     * - 조회 성공 시 사용자 정보 로그 출력
     * - 조회 실패 시 예외 메시지 로그 출력 후 null 반환
     */
    override suspend fun getUser(): UserEntity? {
        return try {
            Timber.d("getUser() 호출 - 사용자 정보 조회")
            val user = userDao.getUser()?.toData()
            if (user != null) {
                Timber.d("getUser() 성공 - 조회된 사용자: ${user.email}")
            } else {
                Timber.d("getUser() - 저장된 사용자 없음")
            }
            user
        } catch (e: Exception) {
            Timber.e(e, "getUser() 실패 - 사용자 조회 중 오류 발생")
            null
        }
    }

    /**
     * 사용자 정보를 로컬 데이터베이스에 저장한다.
     * - 저장할 사용자 이메일을 로그 출력
     * - 저장 성공/실패 여부를 기록
     */
    override suspend fun saveUser(userEntity: UserEntity) {
        try {
            Timber.d("saveUser() 호출 - 저장할 사용자: ${userEntity.email}")
            userDao.saveUser(userEntity.toLocal())
            Timber.d("saveUser() 성공 - 사용자 저장 완료")
        } catch (e: Exception) {
            Timber.e(e, "saveUser() 실패 - 사용자 저장 중 오류 발생")
        }
    }
}