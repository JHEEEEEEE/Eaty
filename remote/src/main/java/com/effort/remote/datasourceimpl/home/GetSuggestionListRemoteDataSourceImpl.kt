package com.effort.remote.datasourceimpl.home

import com.effort.data.datasource.home.GetSuggestionListRemoteDataSource
import com.effort.data.model.home.suggestion.KeywordEntity
import com.effort.remote.service.home.SuggestionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class GetSuggestionListRemoteDataSourceImpl @Inject constructor(
    private val suggestionService: SuggestionService
) : GetSuggestionListRemoteDataSource {

    override fun getSuggestions(query: String): Flow<List<KeywordEntity>> {
        return suggestionService.getSuggestions(query).map { wrapperResponse ->
            val keywordEntities = wrapperResponse.resultKeywords.map { it.toData() }
            Timber.d("KeywordEntities: $keywordEntities")
            keywordEntities
        }
    }
}