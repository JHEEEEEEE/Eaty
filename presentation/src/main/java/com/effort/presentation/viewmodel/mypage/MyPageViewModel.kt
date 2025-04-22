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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val observeUserUpdateUseCase: ObserveUserUpdateUseCase
) : ViewModel() {

    private val _userUpdateState = MutableStateFlow<UiState<FirebaseUserModel>>(UiState.Empty)
    val userUpdateState get() = _userUpdateState.asStateFlow()

    init {
        Timber.d("MyPageViewModel 초기화 - 사용자 정보 감지 시작")
        observeUserUpdate()
    }

    /**
     * 사용자의 정보를 실시간으로 감지하여 UI에 반영한다.
     * - `ObserveUserUpdateUseCase`를 통해 사용자 상태를 지속적으로 감시
     * - 상태 변경이 발생하면 `UiState`를 업데이트하여 UI에서 즉시 반영
     */
    private fun observeUserUpdate() {
        Timber.d("observeUserUpdate() - 사용자 정보 감지 시작")

        viewModelScope.launch {
            observeUserUpdateUseCase()
                .collectLatest { dataResource ->
                    when (dataResource) {
                        is DataResource.Success -> {
                            Timber.d("observeUserUpdate() - 사용자 정보 업데이트 성공: ${dataResource.data}")
                            _userUpdateState.value = UiState.Success(dataResource.data.toPresentation())
                        }
                        is DataResource.Error -> {
                            Timber.e(dataResource.throwable, "observeUserUpdate() - 사용자 정보 업데이트 실패")
                            _userUpdateState.value = UiState.Error(dataResource.throwable)
                        }
                        is DataResource.Loading -> {
                            Timber.d("observeUserUpdate() - 사용자 정보 로딩 중")
                            _userUpdateState.value = UiState.Loading
                        }
                    }
                }
        }
    }
}