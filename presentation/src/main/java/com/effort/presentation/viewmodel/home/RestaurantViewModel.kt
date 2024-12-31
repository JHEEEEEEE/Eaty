package com.effort.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _getRestaurantState = MutableStateFlow<UiState<List<RestaurantModel>>>(UiState.Empty)
    val getRestaurantState = _getRestaurantState.asStateFlow()

    // 기본 정렬 기준: 거리순 (Presentation 모델 사용)
    private val _sortType = MutableStateFlow(SortTypeModel.DISTANCE) // Presentation Model로 변경
    val sortType = _sortType.asStateFlow() // 외부 접근 제한

    // 식당 조회 메서드
    fun fetchRestaurants(query: String) {
        viewModelScope.launch {
            // sortType 변경 시 최신 상태 반영
            _sortType.flatMapLatest { currentSortType ->
                // UseCase에 Domain Model로 변환하여 전달
                getRestaurantListUseCase(query, currentSortType.toDomain())
            }.onStart {
                setLoadingState(_getRestaurantState) // 로딩 상태 처리
            }.onCompletion { cause ->
                handleCompletionState(_getRestaurantState, cause) // 완료 상태 처리
            }.collectLatest { dataResource ->
                // 결과를 UI 상태로 변환하여 업데이트
                _getRestaurantState.value = dataResource.toUiStateList { it.toPresentation() }
            }
        }
    }

    // 정렬 기준 변경 메서드
    fun updateSortType(newSortType: SortTypeModel) {
        _sortType.value = newSortType // 정렬 기준 업데이트
    }
}