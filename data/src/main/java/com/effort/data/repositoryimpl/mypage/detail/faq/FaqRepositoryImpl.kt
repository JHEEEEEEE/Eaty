package com.effort.data.repositoryimpl.mypage.detail.faq

import com.effort.data.datasource.mypage.detail.faq.FaqRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.faq.Faq
import com.effort.domain.repository.mypage.detail.faq.FaqRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class FaqRepositoryImpl @Inject constructor(
    private val faqRemoteDataSource: FaqRemoteDataSource
) : FaqRepository {

    /**
     * Firestore에서 FAQ 목록을 가져온다.
     * - 데이터 로딩 중 상태를 방출
     * - 성공 시 FAQ 리스트를 변환하여 방출
     * - 실패 시 예외를 로깅하고 에러 상태를 방출
     */
    override fun getFaqs(): Flow<DataResource<List<Faq>>> = flow {
        Timber.d("getFaqs() 호출")

        emit(DataResource.loading())

        try {
            val faqs = faqRemoteDataSource.getFaqs().map { it.toDomain() }
            Timber.d("FAQ 데이터 조회 성공 - 개수: ${faqs.size}")

            emit(DataResource.success(faqs))
        } catch (e: Exception) {
            Timber.e(e, "FAQ 데이터 조회 실패")
            emit(DataResource.error(e))
        }
    }
}