package com.effort.data.repositoryimpl.home

import com.effort.data.datasource.home.GetSuggestionListRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.suggestion.Keyword
import com.effort.domain.repository.home.GetSuggestionListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber
import javax.inject.Inject

class GetSuggestionListRepositoryImpl @Inject constructor(
    private val getSuggestionListRemoteDataSource: GetSuggestionListRemoteDataSource
) : GetSuggestionListRepository {

    override fun getSuggestions(query: String): Flow<DataResource<List<Keyword>>> = channelFlow {
        Timber.d("getSuggestions() 호출됨 - query: $query")

        trySend(DataResource.loading())

        try {
            getSuggestionListRemoteDataSource.getSuggestions(query).collect { suggestionEntities ->
                val keywordEntities = suggestionEntities.map { it.toDomain() }
                Timber.d("추천 검색어 개수: ${keywordEntities.size} | query: $query")

                trySend(DataResource.success(keywordEntities))
            }
        } catch (e: Exception) {
            Timber.e(e, "getSuggestions() 실패 - query: $query")
            trySend(DataResource.error(e))
        }
    }
}