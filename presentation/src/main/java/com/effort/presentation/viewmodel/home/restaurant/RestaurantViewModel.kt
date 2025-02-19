package com.effort.presentation.viewmodel.home.restaurant

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
import timber.log.Timber
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

    private val _filters = MutableStateFlow<List<FilterModel>>(emptyList())
    val filters get() = _filters.asStateFlow()

    private val _selectedFilterState = MutableStateFlow<FilterModel?>(null)
    val selectedFilter get() = _selectedFilterState.asStateFlow()

    private val _sortType = MutableStateFlow(SortTypeModel.DEFAULT)

    private val _newItemLiveData = MutableLiveData<String>()
    val newItemLiveData get() = _newItemLiveData

    private val _cachedData = MutableStateFlow<List<RestaurantModel>>(emptyList())
    val cachedData get() = _cachedData.asStateFlow()

    private var currentPage = 1
    var isLastPage = false
    var isLoading = false
    private var lastQuery: String? = null

    /**
     * 식당 데이터를 조회하고 페이지네이션을 적용한다.
     * - 쿼리가 변경되면 기존 데이터를 초기화
     * - 추가 로딩 여부(loadMore)에 따라 기존 데이터를 유지하거나 갱신
     *
     * @param query 검색어
     * @param loadMore 추가 데이터 로드 여부
     */
    fun fetchRestaurants(query: String, loadMore: Boolean = false) {
        if (query != lastQuery) {
            Timber.d("fetchRestaurants() - 검색어 변경 감지: $query")
            currentPage = 1
            lastQuery = query
            isLastPage = false
            _cachedData.value = emptyList()
            _getRestaurantState.value = UiState.Success(emptyList())
        }

        if (isLoading || (isLastPage && loadMore)) return

        isLoading = true
        if (!loadMore) setLoadingState(_getRestaurantState)

        viewModelScope.launch {
            getRestaurantListUseCase(query, _sortType.value.toDomain(), currentPage).onStart {
                    if (!loadMore) setLoadingState(_getRestaurantState)
                    Timber.d("fetchRestaurants() - 데이터 로딩 시작 (페이지: $currentPage)")
                }.onCompletion { cause ->
                    handleCompletionState(_getRestaurantState, cause)
                    isLoading = false
                    Timber.d("fetchRestaurants() - 데이터 로딩 완료 (페이지: $currentPage)")
                }.collectLatest { dataResource ->
                    when (dataResource) {
                        is DataResource.Success -> {
                            val (restaurants, meta) = dataResource.data
                            val newItems = restaurants.map { it.toPresentation() }
                            val newItemSize = restaurants.size

                            Timber.d("fetchRestaurants() - 새로 로드된 데이터 개수: $newItemSize")
                            sendNewItemMessage(newItemSize)

                            if (loadMore) {
                                addPageData(newItems)
                            } else {
                                _cachedData.value = newItems
                                _getRestaurantState.value = UiState.Success(newItems)
                            }

                            if (meta?.isEnd == true) {
                                isLastPage = true
                                Timber.d("fetchRestaurants() - 마지막 페이지 도달")
                            } else {
                                currentPage++
                            }
                        }

                        is DataResource.Error -> {
                            Timber.e(dataResource.throwable, "fetchRestaurants() - API 요청 실패")
                            _getRestaurantState.value = UiState.Error(dataResource.throwable)
                        }

                        is DataResource.Loading -> {
                            Timber.d("fetchRestaurants() - 데이터 로딩 중...")
                        }
                    }
                }
        }
    }

    /**
     * 새로운 아이템 개수를 LiveData로 전달한다.
     *
     * @param newItems 새로 추가된 아이템 개수
     */
    private fun sendNewItemMessage(newItems: Int) {
        Timber.d("sendNewItemMessage() - 추가된 데이터 개수: $newItems")
        _newItemLiveData.postValue("$newItems 개의 데이터가 추가되었습니다.")
    }

    /**
     * 카메라 초기화 상태를 토글한다.
     */
    fun toggleCameraInitialization() {
        _isCameraInitialized.value = !_isCameraInitialized.value
        Timber.d("toggleCameraInitialization() - 현재 상태: ${_isCameraInitialized.value}")
    }

    /**
     * 필터 리스트를 설정한다.
     *
     * @param filterList 적용할 필터 리스트
     */
    fun setFilters(filterList: List<FilterModel>) {
        Timber.d("setFilters() - 필터 리스트 업데이트: ${filterList.map { it.query }}")
        _filters.value = filterList
    }

    /**
     * 특정 필터를 선택하여 적용한다.
     * - 같은 필터를 다시 선택하는 경우 동작하지 않음.
     * - 필터가 선택되면 해당 필터에 맞는 식당 데이터를 조회
     *
     * @param filterId 선택된 필터 ID
     */
    fun selectFilter(filterId: Int) {
        if (_selectedFilterState.value?.id == filterId) return

        val updatedFilters = _filters.value.map {
            it.copy(isSelected = it.id == filterId)
        }

        _filters.value = updatedFilters

        val selected = updatedFilters.find { it.id == filterId }
        _selectedFilterState.value = selected

        selected?.let {
            Timber.d("selectFilter() - 필터 선택됨: ${it.query}")
            fetchRestaurants(it.query)
        }
    }

    /**
     * 캐시된 데이터를 UI에 반영한다.
     * - 캐시가 존재하면 UI 상태를 업데이트
     */
    fun loadCachedData() {
        if (_cachedData.value.isNotEmpty()) {
            Timber.d("loadCachedData() - 캐시 데이터 로드 완료 (데이터 개수: ${_cachedData.value.size})")
            _getRestaurantState.value = UiState.Success(_cachedData.value)
        }
    }

    /**
     * 페이지네이션을 적용하여 새로운 데이터를 추가한다.
     *
     * @param newData 추가할 새로운 데이터 리스트
     */
    private fun addPageData(newData: List<RestaurantModel>) {
        Timber.d("addPageData() - 새로운 데이터 추가 (개수: ${newData.size})")
        val updatedData = _cachedData.value + newData
        _cachedData.value = updatedData
        _getRestaurantState.value = UiState.Success(updatedData)
    }
}