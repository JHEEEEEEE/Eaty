package com.effort.domain.repository.mypage.detail.faq

import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.faq.Faq
import kotlinx.coroutines.flow.Flow

interface FaqRepository {

    fun getFaqs(): Flow<DataResource<List<Faq>>>
}