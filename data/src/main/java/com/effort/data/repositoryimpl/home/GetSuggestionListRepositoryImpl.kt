package com.effort.data.repositoryimpl.home

import android.util.Log
import com.effort.data.datasource.home.GetSuggestionListRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.suggestion.Keyword
import com.effort.domain.repository.home.GetSuggestionListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetSuggestionListRepositoryImpl @Inject constructor(
    private val getSuggestionListRemoteDataSource: GetSuggestionListRemoteDataSource
): GetSuggestionListRepository {

    override fun getSuggestions(query: String): Flow<DataResource<List<Keyword>>> = channelFlow {
        trySend(DataResource.loading())

        try {
            getSuggestionListRemoteDataSource.getSuggestions(query)
                .collect { suggestionEntities ->
                    val keywordEntities = suggestionEntities.map { it.toDomain() }
                    Log.d("RepositoryImpl", "KeywordEntities: $keywordEntities") // 로그 추가
                    trySend(DataResource.success(keywordEntities))
                }
        } catch (e: Exception) {
            trySend(DataResource.error(e))
        }
    }
}