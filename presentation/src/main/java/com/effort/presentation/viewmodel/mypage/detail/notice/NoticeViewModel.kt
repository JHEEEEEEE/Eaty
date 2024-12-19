package com.effort.presentation.viewmodel.mypage.detail.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.mypage.detail.notice.GetNoticeListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.model.mypage.detail.notice.NoticeModel
import com.effort.presentation.model.mypage.detail.notice.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val getNoticeListUseCase: GetNoticeListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<NoticeModel>>>(UiState.Empty)
    val uiState get() = _uiState.asStateFlow()

    init {
        fetchNotices()
    }

    // 반복되는 중복 로직 줄일 수 있는 방법 강구
    private fun fetchNotices() {
        viewModelScope.launch {
            getNoticeListUseCase()
                .onStart {
                    _uiState.value = UiState.Loading
                }
                .onCompletion {
                    // 로딩 종료 (수정 필요, 로딩 종료했을때 값에 따라 empty or success)
                    if (_uiState.value is UiState.Loading) {
                        _uiState.value = UiState.Empty
                    }
                }
                .collectLatest { dataResource ->
                    _uiState.value = when (dataResource) {
                        is DataResource.Success -> {
                            val data = dataResource.data.map { it.toPresentation() }
                            UiState.Success(data)
                        }

                        is DataResource.Error -> {
                            UiState.Error(dataResource.throwable)
                        }

                        is DataResource.Loading -> {
                            UiState.Loading
                        }
                    }

                }
        }
    }
}