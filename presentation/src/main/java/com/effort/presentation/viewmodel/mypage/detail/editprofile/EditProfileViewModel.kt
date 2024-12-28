package com.effort.presentation.viewmodel.mypage.detail.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.mypage.detail.editprofile.CheckNicknameDuplicatedUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateNicknameUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateProfilePicUseCase
import com.effort.presentation.R
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
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
    private val checkNicknameDuplicatedUseCase: CheckNicknameDuplicatedUseCase,
    private val updateProfilePicUseCase: UpdateProfilePicUseCase
) : ViewModel() {

    private val _updateState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val updateState get() = _updateState.asStateFlow()

    private val _checkNicknameDuplicatedState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val  checkNicknameDuplicatedState get() = _checkNicknameDuplicatedState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri get() = _selectedImageUri.asStateFlow()

    fun setSelectedImageUri(uri: String) {
        _selectedImageUri.value = uri
    }

    fun setEmptyNicknameState() {
        _checkNicknameDuplicatedState.value = UiState.Empty
    }

    fun updateProfile(nickname: String?, profilePictureUri: String?) {
        viewModelScope.launch {
            setLoadingState(_updateState)
            var nicknameUpdated = false
            var profileUpdated = false

            try {
                if (!nickname.isNullOrEmpty()) {
                    val nicknameResult = updateNicknameUseCase(nickname)
                    nicknameUpdated = nicknameResult is DataResource.Success
                }

                if (!profilePictureUri.isNullOrEmpty()) {
                    val profileResult = updateProfilePicUseCase(profilePictureUri)
                    profileUpdated = profileResult is DataResource.Success
                }

                if (nicknameUpdated || profileUpdated) {
                    _updateState.value = UiState.Success(true)
                } else {
                    _updateState.value = UiState.Error(Throwable(R.string.updates_failed.toString()))
                }
            } catch (e: Exception) {
                _updateState.value = UiState.Error(e)
            }
        }
    }

    fun checkNicknameDuplicated(nickname: String) {
        viewModelScope.launch {
            checkNicknameDuplicatedUseCase(nickname)
                .onStart {
                    setLoadingState(_checkNicknameDuplicatedState)
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