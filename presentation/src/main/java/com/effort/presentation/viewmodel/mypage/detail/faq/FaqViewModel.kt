package com.effort.presentation.viewmodel.mypage.detail.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.mypage.detail.faq.GetFaqListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.mypage.detail.faq.FaqModel
import com.effort.presentation.model.mypage.detail.faq.toPresentation
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
class FaqViewModel @Inject constructor(
    private val getFaqListUseCase: GetFaqListUseCase
) : ViewModel() {

    private val _getFaqState = MutableStateFlow<UiState<List<FaqModel>>>(UiState.Empty)
    val getFaqState get() = _getFaqState.asStateFlow()

    init {
        Timber.d("FaqViewModel 초기화 - FAQ 데이터 로드 시작")
        fetchFaqs()
    }

    /**
     * FAQ 목록을 불러온다.
     * - API 요청을 통해 데이터를 가져오고 `UiState`로 관리
     * - 데이터 로딩 상태 및 완료 후 상태를 반영
     */
    private fun fetchFaqs() {
        Timber.d("fetchFaqs() - FAQ 목록 조회 요청 시작")

        viewModelScope.launch {
            getFaqListUseCase().onStart {
                    Timber.d("fetchFaqs() - 로딩 상태로 변경")
                    setLoadingState(_getFaqState)
                }.onCompletion { cause ->
                    Timber.d("fetchFaqs() - 요청 완료, 상태: $cause")
                    handleCompletionState(_getFaqState, cause)
                }.collectLatest { dataResource ->
                    _getFaqState.value = dataResource.toUiStateList {
                        Timber.d("fetchFaqs() - 변환된 FAQ 데이터: $it")
                        it.toPresentation()
                    }
                    Timber.d("fetchFaqs() - 최종 상태 업데이트: $_getFaqState")
                }
        }
    }
}