package com.effort.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.home.GetSuggestionListUseCase
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

    // 카테고리 목록 관리
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories get() = _categories.asStateFlow()

    // 키워드 목록 관리
    private val _getSuggestionState = MutableStateFlow<UiState<List<KeywordModel>>>(UiState.Empty)
    val getSuggestionState = _getSuggestionState.asStateFlow()

    fun setCategories(categoryList: List<CategoryModel>) {
        _categories.value = categoryList
    }

    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            getSuggestionListUseCase(query)
                .onStart {
                    // 로딩 상태를 UiState로 설정
                    setLoadingState(_getSuggestionState)
                }
                .onCompletion { cause ->
                    // Flow 완료 상태 처리 (성공 또는 에러 발생 후 호출)
                    handleCompletionState(_getSuggestionState, cause)
                }
                .collect { dataResource ->
                    // DataResource를 UiState로 변환하여 처리
                    _getSuggestionState.value = dataResource.toUiStateList { keyword ->
                        Log.d("HomeViewModel", "${keyword.toPresentation()}")
                        keyword.toPresentation() // Keyword -> KeywordModel로 변환
                    }
                }
        }
    }
}