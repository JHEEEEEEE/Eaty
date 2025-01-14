package com.effort.presentation.viewmodel.home.detail.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.detail.blog.GetBlogReviewListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.model.home.blog.BlogReviewModel
import com.effort.presentation.model.home.blog.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantInfoViewModel @Inject constructor(
    val getBlogReviewListUseCase: GetBlogReviewListUseCase
) : ViewModel() {

    private val _getBlogReviewState =
        MutableStateFlow<UiState<List<BlogReviewModel>>>(UiState.Empty)
    val getBlogReviewState get() = _getBlogReviewState.asStateFlow()

    private var currentPage = 1
    var isLastPage = false
    var isLoading = false

    fun fetchBlogReviews(query: String, region: String, loadMore: Boolean = false) {
        // 1. 추가 로딩 조건 확인
        if (isLoading || (isLastPage && loadMore)) return

        // 2. 로딩 상태 시작
        isLoading = true
        if (!loadMore) {
            setLoadingState(_getBlogReviewState) // 초기 로딩 상태
        }

        viewModelScope.launch {
            when (val dataResource = getBlogReviewListUseCase(query, region, currentPage)) {
                is DataResource.Success -> {
                    val (reviews, meta) = dataResource.data
                    val currentData = if (loadMore) {
                        (_getBlogReviewState.value as? UiState.Success)?.data.orEmpty()
                    } else {
                        emptyList()
                    }

                    val combinedData = currentData + reviews.map { it.toPresentation() }

                    _getBlogReviewState.value = UiState.Success(combinedData)

                    // 5. 페이지네이션 업데이트
                    if (meta?.isEnd == true) { // 마지막 페이지 확인
                        isLastPage = true
                    } else {
                        currentPage++ // 다음 페이지 증가
                    }
                }

                is DataResource.Error -> {
                    Log.e("BlogReviewViewModel", "fetchBlogReviews 실패: ${dataResource.throwable}")
                    _getBlogReviewState.value = UiState.Error(dataResource.throwable)
                }

                is DataResource.Loading -> {
                    Log.d("BlogReviewViewModel", "fetchBlogReviews 로딩 중")
                    _getBlogReviewState.value = UiState.Loading
                }
            }
            isLoading = false
        }
    }
}