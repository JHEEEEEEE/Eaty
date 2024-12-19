package com.effort.presentation.viewmodel.mypage.detail.editprofile

import android.provider.ContactsContract.Data
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateNicknameUseCase
import com.effort.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateNicknameUseCase: UpdateNicknameUseCase
) : ViewModel() {

    private val _updateNicknameState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val updateNicknameState get() = _updateNicknameState.asStateFlow()

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
}