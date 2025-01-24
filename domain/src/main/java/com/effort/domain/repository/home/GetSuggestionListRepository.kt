package com.effort.domain.repository.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.suggestion.Keyword
import kotlinx.coroutines.flow.Flow

interface GetSuggestionListRepository {

    fun getSuggestions(query: String): Flow<DataResource<List<Keyword>>>
}