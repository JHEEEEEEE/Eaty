package com.effort.presentation.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.mypage.ObserveUserUpdateUseCase
import com.effort.presentation.UiState
import com.effort.presentation.model.auth.FirebaseUserModel
import com.effort.presentation.model.auth.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val observeUserUpdateUseCase: ObserveUserUpdateUseCase
) : ViewModel() {

    private val _userUpdateState = MutableStateFlow<UiState<FirebaseUserModel>>(UiState.Empty)
    val userUpdateState get() = _userUpdateState.asStateFlow()

    init {
        observeUserUpdate()
    }

    /**
     * 사용자의 정보를 실시간으로 감지하여 UI에 반영한다.
     * - `ObserveUserUpdateUseCase`를 통해 사용자 상태를 지속적으로 감시
     * - 상태 변경이 발생하면 `UiState`를 업데이트하여 UI에서 즉시 반영
     */
    private fun observeUserUpdate() {
        viewModelScope.launch {
            observeUserUpdateUseCase().collectLatest { dataResource ->
                _userUpdateState.value = when (dataResource) {
                    is DataResource.Success -> UiState.Success(dataResource.data.toPresentation())
                    is DataResource.Error -> UiState.Error(dataResource.throwable)
                    is DataResource.Loading -> UiState.Loading
                }
            }
        }
    }
}