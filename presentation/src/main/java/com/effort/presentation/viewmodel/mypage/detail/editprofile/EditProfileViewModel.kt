package com.effort.presentation.viewmodel.mypage.detail.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.mypage.detail.editprofile.CheckNicknameDuplicatedUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateNicknameUseCase
import com.effort.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateNicknameUseCase: UpdateNicknameUseCase,
    private val checkNicknameDuplicatedUseCase: CheckNicknameDuplicatedUseCase
) : ViewModel() {

    private val _updateNicknameState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val updateNicknameState get() = _updateNicknameState.asStateFlow()

    private val _checkNicknameDuplicatedState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val checkNicknameDuplicatedState get() = _checkNicknameDuplicatedState.asStateFlow()

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            _updateNicknameState.value = UiState.Loading

            val datasource = updateNicknameUseCase(nickname)
            _updateNicknameState.value = when (datasource) {
                is DataResource.Success -> {
                    UiState.Success(true)
                }

                is DataResource.Error -> {
                    UiState.Error(datasource.throwable)
                }

                is DataResource.Loading -> {
                    UiState.Loading
                }
            }
        }
    }


    fun checkNicknameDuplicated(nickname: String) {
        viewModelScope.launch {
            checkNicknameDuplicatedUseCase(nickname)
                .onStart {
                    _checkNicknameDuplicatedState.value = UiState.Loading
                }
                .catch { exception ->
                    _checkNicknameDuplicatedState.value = UiState.Error(exception)
                }
                .collectLatest { datasource ->
                    when (datasource) {
                        is DataResource.Success -> {
                            _checkNicknameDuplicatedState.value = UiState.Success(datasource.data)
                        }

                        is DataResource.Error -> {
                            _checkNicknameDuplicatedState.value = UiState.Error(datasource.throwable)
                        }

                        is DataResource.Loading -> {
                            _checkNicknameDuplicatedState.value = UiState.Loading
                        }
                    }
                }
        }
    }
}