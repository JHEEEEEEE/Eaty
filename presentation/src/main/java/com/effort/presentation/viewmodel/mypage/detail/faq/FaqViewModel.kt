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
import javax.inject.Inject

@HiltViewModel
class FaqViewModel @Inject constructor(
    private val getFaqListUseCase: GetFaqListUseCase
) : ViewModel() {

    private val _getFaqState = MutableStateFlow<UiState<List<FaqModel>>>(UiState.Empty)
    val getFaqState get() = _getFaqState.asStateFlow()

    init {
        fetchFaqs()
    }

    private fun fetchFaqs() {
        viewModelScope.launch {
            getFaqListUseCase()
                .onStart {
                    // 로딩 상태로 업데이트
                    setLoadingState(_getFaqState)
                }
                .onCompletion { cause ->
                    // 로딩 종료
                    handleCompletionState(_getFaqState, cause)
                }
                .collectLatest { dataResource ->
                    _getFaqState.value = dataResource.toUiStateList { it.toPresentation() }
                }
        }
    }
}