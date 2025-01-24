package com.effort.remote.service.home

import com.effort.remote.model.home.suggestion.KeywordWrapperResponse
import kotlinx.coroutines.flow.Flow

interface SuggestionService {

    fun getSuggestions(query: String): Flow<KeywordWrapperResponse>
}