package com.effort.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.home.suggestion.GetSuggestionListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.CategoryModel
import com.effort.presentation.model.home.KeywordModel
import com.effort.presentation.model.home.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSuggestionListUseCase: GetSuggestionListUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories get() = _categories.asStateFlow()

    private val _getSuggestionState = MutableStateFlow<UiState<List<KeywordModel>>>(UiState.Empty)
    val getSuggestionState = _getSuggestionState.asStateFlow()

    /**
     * 카테고리 목록을 설정한다.
     *
     * @param categoryList 적용할 카테고리 리스트
     */
    fun setCategories(categoryList: List<CategoryModel>) {
        _categories.value = categoryList
    }

    /**
     * 키워드 추천 목록을 조회한다.
     * - 쿼리에 해당하는 추천 키워드를 검색하여 반환
     * - 로딩 상태, 성공/실패 상태를 관리
     *
     * @param query 검색할 키워드
     */
    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            getSuggestionListUseCase(query)
                .onStart {
                    setLoadingState(_getSuggestionState)
                }
                .onCompletion { cause ->
                    handleCompletionState(_getSuggestionState, cause)
                }
                .collect { dataResource ->
                    _getSuggestionState.value = dataResource.toUiStateList { keyword ->
                        Log.d("HomeViewModel", "추천 키워드 변환: ${keyword.toPresentation()}")
                        keyword.toPresentation()
                    }
                }
        }
    }
}