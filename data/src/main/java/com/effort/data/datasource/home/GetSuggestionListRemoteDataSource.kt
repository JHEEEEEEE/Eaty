package com.effort.data.datasource.home

import com.effort.data.model.home.suggestion.KeywordEntity
import kotlinx.coroutines.flow.Flow

interface GetSuggestionListRemoteDataSource {

    fun getSuggestions(query: String): Flow<List<KeywordEntity>>
}