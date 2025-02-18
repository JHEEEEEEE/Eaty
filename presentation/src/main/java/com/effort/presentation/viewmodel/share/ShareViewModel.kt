package com.effort.presentation.viewmodel.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.share.ShareUseCase
import com.effort.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val shareUseCase: ShareUseCase
) : ViewModel() {

    private val _shareLinkState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val shareLinkState get() = _shareLinkState.asStateFlow()

    /**
     * 공유 링크 생성 요청한다.
     * - 식당 정보를 바탕으로 공유할 URL을 생성
     * - `shareUseCase`를 호출하여 서버에서 공유 링크를 생성하고 상태 업데이트
     * - 성공 시 `UiState.Success`로 링크 반환, 실패 시 `UiState.Error`로 오류 전달
     */
    fun shareContent(
        title: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ) {
        viewModelScope.launch {
            when (val dataResource = shareUseCase(
                title,
                lotNumberAddress,
                roadNameAddress,
                distance,
                phoneNumber,
                placeUrl
            )) {
                is DataResource.Success -> {
                    val shareUrl = dataResource.data
                    _shareLinkState.value = UiState.Success(shareUrl)
                }

                is DataResource.Error -> {
                    _shareLinkState.value = UiState.Error(dataResource.throwable)
                }

                is DataResource.Loading -> {
                    _shareLinkState.value = UiState.Loading
                }
            }
        }
    }

    /**
     * 공유 상태 초기화한다.
     * - 공유가 완료된 후 `_shareLinkState`를 `UiState.Empty`로 변경하여 상태를 초기화
     * - 이후 새로운 공유 요청을 받을 수 있도록 함
     */
    fun resetShareState() {
        _shareLinkState.value = UiState.Empty
    }
}