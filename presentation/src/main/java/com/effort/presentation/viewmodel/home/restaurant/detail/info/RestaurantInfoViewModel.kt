package com.effort.presentation.viewmodel.home.restaurant.detail.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.detail.blog.GetBlogReviewListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.model.home.restaurant.detail.blog.BlogReviewModel
import com.effort.presentation.model.home.restaurant.detail.blog.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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

    /**
     * 블로그 리뷰 데이터 요청한다.
     * - 최초 로딩 또는 추가 로딩을 수행
     * - `loadMore` 값이 `true`이면 기존 데이터에 이어서 추가 페이지 요청
     * - 마지막 페이지거나 이미 로딩 중이면 추가 요청하지 않음
     *
     * @param query 검색어 (식당명)
     * @param region 검색 지역
     * @param loadMore 추가 데이터 로딩 여부
     */
    fun fetchBlogReviews(query: String, region: String, loadMore: Boolean = false) {
        if (isLoading || (isLastPage && loadMore)) return

        isLoading = true
        if (!loadMore) {
            setLoadingState(_getBlogReviewState)
        }

        viewModelScope.launch {
            Timber.d("fetchBlogReviews() 호출: query=$query, region=$region, page=$currentPage, loadMore=$loadMore")

            when (val dataResource = getBlogReviewListUseCase(query, region, currentPage)) {
                is DataResource.Success -> {
                    val (reviews, meta) = dataResource.data
                    Timber.d("fetchBlogReviews() 성공: ${reviews.size}개 데이터 로드됨")

                    // 기존 데이터와 새 데이터 결합 (추가 로딩이면 기존 데이터 유지)
                    val currentData = if (loadMore) {
                        (_getBlogReviewState.value as? UiState.Success)?.data.orEmpty()
                    } else {
                        emptyList()
                    }
                    val combinedData = currentData + reviews.map { it.toPresentation() }

                    _getBlogReviewState.value = UiState.Success(combinedData)

                    if (meta?.isEnd == true) {
                        isLastPage = true
                        Timber.d("fetchBlogReviews() 마지막 페이지 도달")
                    } else {
                        currentPage++
                        Timber.d("fetchBlogReviews() 다음 페이지: $currentPage")
                    }
                }

                is DataResource.Error -> {
                    Timber.e(dataResource.throwable, "fetchBlogReviews() 실패")
                    _getBlogReviewState.value = UiState.Error(dataResource.throwable)
                }

                is DataResource.Loading -> {
                    Timber.d("fetchBlogReviews() 로딩 중")
                    _getBlogReviewState.value = UiState.Loading
                }
            }
            isLoading = false
        }
    }
}