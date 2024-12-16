package com.effort.data.repositoryimpl

import com.effort.data.datasource.FaqRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.Faq
import com.effort.domain.repository.FaqRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FaqRepositoryImpl @Inject constructor(
    private val faqRemoteDataSource: FaqRemoteDataSource
): FaqRepository {
    override fun getFaqs(): Flow<DataResource<List<Faq>>> = flow {
        emit(DataResource.loading())

        try {
            val faqs = faqRemoteDataSource.getFaqs().map { it.toDomain() }
            emit(DataResource.success(faqs))
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}
