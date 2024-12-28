package com.effort.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.home.GetRestaurantListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.RestaurantModel
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
): ViewModel() {

    private val _getRestaurantState = MutableStateFlow<UiState<List<RestaurantModel>>>(UiState.Empty)
    val getRestaurantState get() = _getRestaurantState.asStateFlow()

    fun fetchRestaurants(query: String) {
        viewModelScope.launch {
            Log.d("RestaurantViewModel", "fetchRestaurants() - Query: $query") // 로그 추가

            getRestaurantListUseCase(query)
                .onStart {
                    Log.d("RestaurantViewModel", "fetchRestaurants() - Loading Start") // 시작 로그
                    setLoadingState(_getRestaurantState)
                }
                .onCompletion { cause ->
                    Log.d("RestaurantViewModel", "fetchRestaurants() - Completion. Cause: $cause") // 완료 로그
                    handleCompletionState(_getRestaurantState, cause)
                }
                .collectLatest { dataResource ->
                    Log.d("RestaurantViewModel", "fetchRestaurants() - Collected: $dataResource") // 데이터 상태 로그
                    _getRestaurantState.value = dataResource.toUiStateList {
                        Log.d("RestaurantViewModel", "fetchRestaurants() - Transforming data: $it") // 변환 중 로그
                        it.toPresentation()
                    }
                }
        }
    }
}