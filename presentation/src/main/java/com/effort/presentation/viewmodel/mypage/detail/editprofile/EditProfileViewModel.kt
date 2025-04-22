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
import timber.log.Timber
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
        Timber.d("setSelectedImageUri() - 선택된 이미지 URI: $uri")
    }

    /**
     * 닉네임 중복 확인 상태를 초기화한다.
     */
    fun setEmptyNicknameState() {
        _checkNicknameDuplicatedState.value = UiState.Empty
        Timber.d("setEmptyNicknameState() - 닉네임 중복 확인 상태 초기화")
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
        Timber.d("updateProfile() - 닉네임: $nickname, 프로필 사진 URI: $profilePictureUri")

        viewModelScope.launch {
            setLoadingState(_updateState)
            var nicknameUpdated = false
            var profileUpdated = false

            try {
                if (!nickname.isNullOrEmpty()) {
                    Timber.d("updateProfile() - 닉네임 변경 요청: $nickname")
                    val nicknameResult = updateNicknameUseCase(nickname)
                    nicknameUpdated = nicknameResult is DataResource.Success
                    Timber.d("updateProfile() - 닉네임 변경 결과: $nicknameUpdated")
                }

                if (!profilePictureUri.isNullOrEmpty()) {
                    Timber.d("updateProfile() - 프로필 사진 변경 요청: $profilePictureUri")
                    val profileResult = updateProfilePicUseCase(profilePictureUri)
                    profileUpdated = profileResult is DataResource.Success
                    Timber.d("updateProfile() - 프로필 사진 변경 결과: $profileUpdated")
                }

                if (nicknameUpdated || profileUpdated) {
                    Timber.d("updateProfile() - 업데이트 성공")
                    _updateState.value = UiState.Success(true)
                } else {
                    Timber.e("updateProfile() - 변경 사항 없음 또는 실패")
                    _updateState.value =
                        UiState.Error(Throwable(R.string.updates_failed.toString()))
                }
            } catch (e: Exception) {
                Timber.e(e, "updateProfile() - 예외 발생")
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
        Timber.d("checkNicknameDuplicated() - 닉네임 중복 확인 요청: $nickname")

        viewModelScope.launch {
            checkNicknameDuplicatedUseCase(nickname).onStart {
                    setLoadingState(_checkNicknameDuplicatedState)
                    Timber.d("checkNicknameDuplicated() - 로딩 상태로 변경")
                }.catch { exception ->
                    Timber.e(exception, "checkNicknameDuplicated() - 예외 발생")
                    _checkNicknameDuplicatedState.value = UiState.Error(exception)
                }.collectLatest { datasource ->
                    _checkNicknameDuplicatedState.value = when (datasource) {
                        is DataResource.Success -> {
                            Timber.d("checkNicknameDuplicated() - 결과: ${datasource.data}")
                            UiState.Success(datasource.data)
                        }

                        is DataResource.Error -> {
                            Timber.e(datasource.throwable, "checkNicknameDuplicated() - 오류 발생")
                            UiState.Error(datasource.throwable)
                        }

                        is DataResource.Loading -> {
                            Timber.d("checkNicknameDuplicated() - 로딩 중")
                            UiState.Loading
                        }
                    }
                }
        }
    }
}