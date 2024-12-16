package com.effort.domain.repository

import com.effort.domain.DataResource
import com.effort.domain.model.Faq
import kotlinx.coroutines.flow.Flow

interface FaqRepository {

    fun getFaqs(): Flow<DataResource<List<Faq>>>
}