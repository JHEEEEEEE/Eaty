package com.effort.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.GetFaqListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.model.FaqModel
import com.effort.presentation.model.toPresentation
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
): ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<FaqModel>>>(UiState.Empty)
    val uiState get() = _uiState.asStateFlow()

    init {
        fetchFaqs()
    }

    private fun fetchFaqs() {
        viewModelScope.launch {
            getFaqListUseCase()
                .onStart {
                    // 로딩 상태로 업데이트
                    _uiState.value = UiState.Loading
                }
                .onCompletion {
                    // 로딩 종료
                    if (_uiState.value is UiState.Loading) {
                        _uiState.value = UiState.Empty
                    }
                }
                .collectLatest { dataResource ->
                    when (dataResource) {
                        is DataResource.Success -> {
                            _uiState.value = UiState.Success(
                                data = dataResource.data.map { it.toPresentation() }
                            )
                        }

                        is DataResource.Error -> {
                            _uiState.value = UiState.Error(dataResource.throwable)
                        }

                        is DataResource.Loading -> {
                            // 로딩 상태 처리 (추가적인 로딩 상태가 필요하면 사용 가능)
                            _uiState.value = UiState.Loading
                        }
                    }
                }
        }
    }
}