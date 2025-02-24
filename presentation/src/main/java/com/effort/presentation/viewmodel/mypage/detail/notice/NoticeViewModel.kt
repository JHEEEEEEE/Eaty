package com.effort.presentation.viewmodel.mypage.detail.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.mypage.detail.notice.GetNoticeListUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.handleCompletionState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.mypage.detail.notice.NoticeModel
import com.effort.presentation.model.mypage.detail.notice.toPresentation
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
class NoticeViewModel @Inject constructor(
    private val getNoticeListUseCase: GetNoticeListUseCase
) : ViewModel() {

    private val _getNoticeState = MutableStateFlow<UiState<List<NoticeModel>>>(UiState.Empty)
    val getNoticeState get() = _getNoticeState.asStateFlow()

    init {
        Timber.d("NoticeViewModel 초기화 - 공지사항 데이터 로드 시작")
        fetchNotices()
    }

    /**
     * 공지사항 목록을 불러온다.
     * - API 요청을 통해 데이터를 가져오고 `UiState`로 관리
     * - 로딩 상태와 데이터 로딩 완료 후의 상태를 반영
     */
    private fun fetchNotices() {
        Timber.d("fetchNotices() - 공지사항 목록 조회 요청 시작")

        viewModelScope.launch {
            getNoticeListUseCase().onStart {
                    Timber.d("fetchNotices() - 로딩 상태로 변경")
                    setLoadingState(_getNoticeState)
                }.onCompletion { cause ->
                    Timber.d("fetchNotices() - 요청 완료, 상태: $cause")
                    handleCompletionState(_getNoticeState, cause)
                }.collectLatest { dataResource ->
                    _getNoticeState.value = dataResource.toUiStateList {
                        Timber.d("fetchNotices() - 변환된 공지사항 데이터: $it")
                        it.toPresentation()
                    }
                    Timber.d("fetchNotices() - 최종 상태 업데이트: $_getNoticeState")
                }
        }
    }
}