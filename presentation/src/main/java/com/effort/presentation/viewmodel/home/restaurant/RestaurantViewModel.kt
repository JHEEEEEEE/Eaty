package com.effort.presentation.viewmodel.home.restaurant

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.GetRestaurantListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.model.home.restaurant.SortTypeModel
import com.effort.presentation.model.home.restaurant.toDomain
import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.effort.presentation.model.home.restaurant.toPresentation
import com.effort.presentation.model.map.FilterModel
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

    private val _getRestaurantState =
        MutableStateFlow<UiState<List<RestaurantModel>>>(UiState.Empty)
    val getRestaurantState get() = _getRestaurantState.asStateFlow()

    private val _isCameraInitialized = MutableStateFlow(false)
    val isCameraInitialized get() = _isCameraInitialized.asStateFlow()

    // 필터 목록 관리
    private val _filters = MutableStateFlow<List<FilterModel>>(emptyList())
    val filters get() = _filters.asStateFlow()

    // 선택된 필터
    private val _selectedFilterState = MutableStateFlow<FilterModel?>(null)
    val selectedFilter get() = _selectedFilterState.asStateFlow()

    private val _sortType = MutableStateFlow(SortTypeModel.DEFAULT)

    private val _newItemLiveData = MutableLiveData<String>()
    val newItemLiveData get() = _newItemLiveData

    private val _cachedData = MutableStateFlow<List<RestaurantModel>>(emptyList())
    val cachedData get() = _cachedData.asStateFlow() // 캐시된 데이터를 외부에서 관찰 가능

    // 페이지네이션 상태 관리
    private var currentPage = 1
    var isLastPage = false
    var isLoading = false

    private var lastQuery: String? = null // 마지막 쿼리값을 저장

    fun fetchRestaurants(query: String, loadMore: Boolean = false) {
        // 0. 쿼리 변경 시 상태 초기화
        if (query != lastQuery) {
            currentPage = 1
            lastQuery = query
            isLastPage = false
            _cachedData.value = emptyList() // 캐시 초기화
            _getRestaurantState.value = UiState.Success(emptyList()) // 기존 데이터 초기화
        }

        // 1. 로딩 조건 확인
        if (isLoading || (isLastPage && loadMore)) return

        // 2. 로딩 시작
        isLoading = true
        if (!loadMore) setLoadingState(_getRestaurantState)

        viewModelScope.launch {
            getRestaurantListUseCase(query, _sortType.value.toDomain(), currentPage)
                .onStart { if (!loadMore) setLoadingState(_getRestaurantState) }
                .onCompletion { cause ->
                    handleCompletionState(_getRestaurantState, cause)
                    isLoading = false
                }
                .collectLatest { dataResource ->
                    when (dataResource) {
                        is DataResource.Success -> {
                            val (restaurants, meta) = dataResource.data
                            val newItems = restaurants.map { it.toPresentation() }
                            val newItemSize = restaurants.size

                            // 새로 로드된 데이터 개수 저장
                            Log.d("MapViewModel", "새로 로드된 데이터 개수: $newItemSize")
                            sendNewItemMessage(newItemSize)

                            // 기존 데이터와 결합하여 캐시 업데이트
                            if (loadMore) {
                                addPageData(newItems)  // 추가 데이터 로드 시 addPageData 사용
                            } else {
                                _cachedData.value = newItems  // 첫 페이지 로드 시 캐시 초기화
                                _getRestaurantState.value = UiState.Success(newItems)
                            }


                            if (meta?.isEnd == true) {
                                isLastPage = true
                            } else {
                                currentPage++
                            }
                        }

                        is DataResource.Error -> _getRestaurantState.value =
                            UiState.Error(dataResource.throwable)

                        is DataResource.Loading -> {}
                    }
                }
        }
    }

    private fun sendNewItemMessage(newItems: Int) {
        _newItemLiveData.postValue("$newItems 개의 데이터가 추가되었습니다.")
    }

    fun toggleCameraInitialization() {
        _isCameraInitialized.value = !_isCameraInitialized.value
    }

    fun setFilters(filterList: List<FilterModel>) {
        _filters.value = filterList
    }

    fun selectFilter(filterId: Int) {
        if (_selectedFilterState.value?.id == filterId) return // 같은 필터 선택 방지

        val updatedFilters = _filters.value.map {
            it.copy(isSelected = it.id == filterId) // 선택된 필터만 true로 설정
        }

        _filters.value = updatedFilters

        val selected = updatedFilters.find { it.id == filterId }
        _selectedFilterState.value = selected

        selected?.let {
            Log.d("MapViewModel", "필터 선택됨: ${it.query}")  // 필터 선택 로그
            fetchRestaurants(it.query) // 필터 변경 시 데이터 로드
        }
    }

    fun loadCachedData() {
        // 캐시된 데이터를 UI 상태로 전달
        if (_cachedData.value.isNotEmpty()) {
            _getRestaurantState.value = UiState.Success(_cachedData.value)
        }
    }

    private fun addPageData(newData: List<RestaurantModel>) {
        val updatedData = _cachedData.value + newData
        _cachedData.value = updatedData
        _getRestaurantState.value = UiState.Success(updatedData)
    }
}