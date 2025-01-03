package com.effort.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.GetRestaurantListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.model.home.RestaurantModel
import com.effort.presentation.model.home.SortTypeModel
import com.effort.presentation.model.home.toDomain
import com.effort.presentation.model.home.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val getRestaurantListUseCase: GetRestaurantListUseCase
) : ViewModel() {

    // 상태 관리
    private val _getRestaurantState =
        MutableStateFlow<UiState<List<RestaurantModel>>>(UiState.Empty)
    val getRestaurantState get() = _getRestaurantState.asStateFlow()

    private val _sortType = MutableStateFlow(SortTypeModel.DEFAULT)
    val sortType get() = _sortType.asStateFlow()

    // 페이지네이션 상태 관리
    private var currentPage = 1
    var isLastPage = false
    var isLoading = false

    // 식당 조회 메서드 (페이지네이션 적용)
    fun fetchRestaurants(query: String, loadMore: Boolean = false) {
        // 1. 추가 로딩 조건 확인
        if (isLoading || (isLastPage && loadMore)) return

        // 2. 로딩 상태 시작
        isLoading = true
        if (!loadMore) {
            setLoadingState(_getRestaurantState) // 초기 로딩 상태
        }

        viewModelScope.launch {
            getRestaurantListUseCase(
                query,
                _sortType.value.toDomain(),
                currentPage
            ).onStart {
                if (!loadMore) setLoadingState(_getRestaurantState)
            }.onCompletion { cause ->
                handleCompletionState(_getRestaurantState, cause)
                isLoading = false
            }.collectLatest { dataResource ->
                when (dataResource) {
                    is DataResource.Success -> {
                        // 3. 데이터와 메타 정보 분리
                        val (restaurants, meta) = dataResource.data

                        Log.d("RestaurantViewModel", "${dataResource.data.first}")

                        // 4. 데이터 결합 및 상태 업데이트
                        val currentData = if (loadMore) {
                            (_getRestaurantState.value as? UiState.Success)?.data.orEmpty()
                        } else {
                            emptyList()
                        }

                        val combinedData = currentData + restaurants.map { it.toPresentation() }
                        _getRestaurantState.value = UiState.Success(combinedData)

                        // 5. 페이지네이션 업데이트
                        if (meta?.isEnd == true) { // 마지막 페이지 확인
                            isLastPage = true
                        } else {
                            currentPage++ // 다음 페이지 증가
                        }
                    }

                    is DataResource.Error -> {
                        _getRestaurantState.value = UiState.Error(dataResource.throwable)
                    }

                    is DataResource.Loading -> {
                        if (!loadMore) setLoadingState(_getRestaurantState)
                    }
                }
            }
        }
    }

    // 정렬 기준 변경 메서드
    fun updateSortType(newSortType: SortTypeModel) {
        _sortType.value = newSortType
        resetPagination()
    }

    // 페이지네이션 초기화
    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
        isLoading = false
        _getRestaurantState.value = UiState.Empty
    }
}