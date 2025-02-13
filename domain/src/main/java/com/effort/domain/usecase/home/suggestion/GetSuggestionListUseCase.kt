package com.effort.domain.usecase.home.suggestion

import com.effort.domain.DataResource
import com.effort.domain.model.home.suggestion.Keyword
import kotlinx.coroutines.flow.Flow

interface GetSuggestionListUseCase {

    operator fun invoke(query: String): Flow<DataResource<List<Keyword>>>
}