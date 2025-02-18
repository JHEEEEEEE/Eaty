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
    val checkNicknameDuplicatedState get() = _checkNicknameDuplicatedState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri get() = _selectedImageUri.asStateFlow()

    /**
     * 선택한 프로필 이미지 URI를 설정한다.
     *
     * @param uri 선택한 이미지 URI
     */
    fun setSelectedImageUri(uri: String) {
        _selectedImageUri.value = uri
    }

    /**
     * 닉네임 중복 확인 상태를 초기화한다.
     */
    fun setEmptyNicknameState() {
        _checkNicknameDuplicatedState.value = UiState.Empty
    }

    /**
     * 프로필 정보를 업데이트한다.
     * - 닉네임 또는 프로필 사진이 변경되었을 경우 업데이트 수행
     * - 둘 다 변경되지 않으면 에러 상태 반환
     *
     * @param nickname 변경할 닉네임 (선택 사항)
     * @param profilePictureUri 변경할 프로필 사진 URI (선택 사항)
     */
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

    /**
     * 닉네임 중복 여부를 확인한다.
     * - 중복된 경우 `UiState.Success(false)` 반환
     * - 사용 가능한 경우 `UiState.Success(true)` 반환
     *
     * @param nickname 중복 확인할 닉네임
     */
    fun checkNicknameDuplicated(nickname: String) {
        viewModelScope.launch {
            checkNicknameDuplicatedUseCase(nickname)
                .onStart { setLoadingState(_checkNicknameDuplicatedState) }
                .catch { exception -> _checkNicknameDuplicatedState.value = UiState.Error(exception) }
                .collectLatest { datasource ->
                    _checkNicknameDuplicatedState.value = when (datasource) {
                        is DataResource.Success -> UiState.Success(datasource.data)
                        is DataResource.Error -> UiState.Error(datasource.throwable)
                        is DataResource.Loading -> UiState.Loading
                    }
                }
        }
    }
}