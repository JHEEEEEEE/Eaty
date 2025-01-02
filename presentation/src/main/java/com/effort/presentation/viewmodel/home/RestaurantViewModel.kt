package com.effort.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.model.home.SortType
import com.effort.domain.usecase.home.GetRestaurantListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.RestaurantModel
import com.effort.presentation.model.home.SortTypeModel
import com.effort.presentation.model.home.toDomain
import com.effort.presentation.model.home.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val getRestaurantListUseCase: GetRestaurantListUseCase // 하나의 UseCase만 사용
) : ViewModel() {

    // 상태 관리
    private val _getRestaurantState =
        MutableStateFlow<UiState<List<RestaurantModel>>>(UiState.Empty)
    val getRestaurantState get() = _getRestaurantState.asStateFlow()

    // 기본 정렬 기준: 거리순 (Presentation 모델 사용)
    private val _sortType = MutableStateFlow(SortTypeModel.DISTANCE) // Presentation Model로 변경
    val sortType get() = _sortType.asStateFlow() // 외부 접근 제한

    // 식당 조회 메서드
    fun fetchRestaurants(query: String) {
        viewModelScope.launch {
            getRestaurantListUseCase(query, _sortType.value.toDomain()) // 최신 정렬 타입 반영
                .onStart {
                    // 로딩 상태 처리
                    setLoadingState(_getRestaurantState)
                }.onCompletion { cause ->
                    // 완료 상태 처리 (에러 또는 성공 처리)
                    handleCompletionState(_getRestaurantState, cause)
                }.collectLatest { dataResource ->
                    // 결과에 따른 UI 상태 처리
                    _getRestaurantState.value = dataResource.toUiStateList { it.toPresentation() }
                }
        }
    }

    // 정렬 기준 변경 메서드
    fun updateSortType(newSortType: SortTypeModel) {
        _sortType.value = newSortType // 정렬 기준 업데이트
    }
}