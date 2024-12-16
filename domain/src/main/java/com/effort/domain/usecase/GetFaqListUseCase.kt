package com.effort.domain.usecase

import com.effort.domain.DataResource
import com.effort.domain.model.Faq
import kotlinx.coroutines.flow.Flow


interface GetFaqListUseCase {

    operator fun invoke(): Flow<DataResource<List<Faq>>>
}