package com.effort.data.repositoryimpl.mypage

import com.effort.data.datasource.mypage.MyPageLocalDataSource
import com.effort.data.datasource.mypage.MyPageRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.auth.User
import com.effort.domain.repository.mypage.MyPageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import timber.log.Timber
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageRemoteDataSource: MyPageRemoteDataSource,
    private val myPageLocalDataSource: MyPageLocalDataSource
) : MyPageRepository {

    /**
     * 사용자 정보를 실시간으로 감지한다.
     * - 로컬 데이터가 있을 경우 먼저 반환
     * - 원격 데이터를 받아와 로컬에 저장 후 반환
     * - 실패 시 예외를 로깅하고, 로컬 데이터를 복구하여 반환
     */
    override fun observeUserUpdate(): Flow<DataResource<User>> = channelFlow {
        Timber.d("observeUserUpdate() 호출됨")

        try {
            val localData = myPageLocalDataSource.getUser()
            localData?.let {
                Timber.d("로컬 데이터 사용: $it")
                send(DataResource.success(it.toDomain()))
            }
        } catch (e: Exception) {
            Timber.e(e, "로컬 데이터 로드 실패")
        }

        myPageRemoteDataSource.observeUserUpdate().retry(2).map { entity ->
                val domainData = entity.toDomain()

                try {
                    myPageLocalDataSource.saveUser(entity)
                    Timber.d("원격 데이터 저장 완료: $domainData")
                } catch (e: Exception) {
                    Timber.e(e, "사용자 데이터 저장 실패")
                }

                DataResource.success(domainData) // 최신 데이터 반환
            }.catch { e ->
                Timber.e(e, "원격 데이터 가져오기 실패")

                val fallbackData = myPageLocalDataSource.getUser()
                if (fallbackData != null) {
                    Timber.d("로컬 데이터로 복구 진행: $fallbackData")
                    send(DataResource.success(fallbackData.toDomain())) // 로컬 데이터 복구
                } else {
                    Timber.e("로컬 데이터 없음, 최종 에러 반환")
                    send(DataResource.error(e)) // 최종 에러 반환
                }
            }.collect { send(it) }
    }
}