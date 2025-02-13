package com.effort.domain.usecaseimpl.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.suggestion.Keyword
import com.effort.domain.repository.home.GetSuggestionListRepository
import com.effort.domain.usecase.home.suggestion.GetSuggestionListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSuggestionListUseCaseImpl @Inject constructor(
    private val getSuggestionListRepository: GetSuggestionListRepository
) : GetSuggestionListUseCase {

    override fun invoke(query: String): Flow<DataResource<List<Keyword>>> {
        return getSuggestionListRepository.getSuggestions(query)
    }
}