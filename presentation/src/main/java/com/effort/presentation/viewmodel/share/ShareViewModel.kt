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

    fun resetShareState() {
        _shareLinkState.value = UiState.Empty // 공유 후 상태 초기화
    }
}