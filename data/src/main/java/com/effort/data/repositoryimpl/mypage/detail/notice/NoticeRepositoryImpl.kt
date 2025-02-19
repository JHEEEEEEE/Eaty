package com.effort.data.repositoryimpl.mypage.detail.notice

import com.effort.data.datasource.mypage.detail.notice.NoticeRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.notice.Notice
import com.effort.domain.repository.mypage.detail.notice.NoticeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(
    private val noticeRemoteDataSource: NoticeRemoteDataSource
) : NoticeRepository {

    /**
     * Firestore에서 공지사항 목록을 가져온다.
     * - 데이터 로딩 중 상태를 방출
     * - 성공 시 공지사항 리스트를 변환하여 방출
     * - 실패 시 예외를 로깅하고 에러 상태를 방출
     */
    override fun getNotices(): Flow<DataResource<List<Notice>>> = flow {
        Timber.d("getNotices() 호출")

        emit(DataResource.loading())

        try {
            val notices = noticeRemoteDataSource.getNotices().map { it.toDomain() }
            Timber.d("공지사항 데이터 조회 성공 - 개수: ${notices.size}")

            emit(DataResource.success(notices))
        } catch (e: Exception) {
            Timber.e(e, "공지사항 데이터 조회 실패")
            emit(DataResource.error(e))
        }
    }
}
