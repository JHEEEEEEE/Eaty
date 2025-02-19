@file:Suppress("DEPRECATION")

package com.effort.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import com.effort.domain.usecase.auth.CheckUserLoggedInUseCase
import com.effort.domain.usecase.auth.SignOutUseCase
import com.effort.presentation.R
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticateUserUseCase: AuthenticateUserUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _authenticateState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val authenticateState get() = _authenticateState.asStateFlow()

    private val _signOutState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val signOutState get() = _signOutState.asStateFlow()

    /**
     * Google 로그인 화면을 호출하는 Intent 반환
     */
    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    /**
     * 사용자가 로그인되어 있는지 확인한다.
     * - Firebase 인증 정보를 기반으로 로그인 여부 확인
     * - 로그인 상태가 변경되면 UI 업데이트를 위해 StateFlow로 상태 전달
     */
    fun checkUserLoggedIn() {
        viewModelScope.launch {
            Timber.d("checkUserLoggedIn() 호출")
            setLoadingState(_authenticateState)

            _authenticateState.value = when (val dataResource = checkUserLoggedInUseCase()) {
                is DataResource.Success -> {
                    Timber.d("checkUserLoggedIn() 성공: ${dataResource.data}")
                    if (dataResource.data) {
                        UiState.Success(Unit)
                    } else {
                        UiState.Error(Throwable(R.string.user_not_logged_in.toString()))
                    }
                }

                is DataResource.Error -> {
                    Timber.e(dataResource.throwable, "checkUserLoggedIn() 실패")
                    UiState.Error(dataResource.throwable)
                }

                is DataResource.Loading -> {
                    Timber.d("checkUserLoggedIn() 로딩 중")
                    UiState.Loading
                }
            }
        }
    }

    /**
     * Google 로그인 결과를 처리하여 Firebase 인증한다.
     * - Google Sign-In 성공 시 ID Token을 Firebase에 전달하여 인증
     * - 인증 성공 시 로그인 상태를 업데이트
     */
    fun handleSignInResult(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                Timber.d("handleSignInResult() 호출: ${account.email}")
                setLoadingState(_authenticateState)

                _authenticateState.value =
                    when (val dataResource = authenticateUserUseCase(account.idToken!!)) {
                        is DataResource.Success -> {
                            Timber.d("handleSignInResult() 성공")
                            if (dataResource.data) {
                                UiState.Success(Unit)
                            } else {
                                UiState.Error(Exception(R.string.authentication_failed.toString()))
                            }
                        }

                        is DataResource.Error -> {
                            Timber.e(dataResource.throwable, "handleSignInResult() 실패")
                            UiState.Error(dataResource.throwable)
                        }

                        is DataResource.Loading -> {
                            Timber.d("handleSignInResult() 로딩 중")
                            UiState.Loading
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "handleSignInResult() 예외 발생")
                UiState.Error(e)
            } finally {
                if (_authenticateState.value is UiState.Loading) {
                    Timber.d("handleSignInResult() 상태 초기화")
                    UiState.Empty
                }
            }
        }
    }

    /**
     * 사용자 로그아웃 처리한다.
     * - Firebase 로그아웃 및 Google 계정 연결 해제
     * - 로그아웃 성공 시 로그인 화면으로 전환
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                Timber.d("signOut() 호출")
                setLoadingState(_signOutState)

                _signOutState.value = when (val dataResource = signOutUseCase()) {
                    is DataResource.Success -> {
                        googleSignInClient.signOut()
                        googleSignInClient.revokeAccess()
                        Timber.d("signOut() 성공")
                        UiState.Success(true)
                    }

                    is DataResource.Error -> {
                        Timber.e(dataResource.throwable, "signOut() 실패")
                        UiState.Error(dataResource.throwable)
                    }

                    is DataResource.Loading -> {
                        Timber.d("signOut() 로딩 중")
                        UiState.Loading
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "signOut() 예외 발생")
                UiState.Error(e)
            }
        }
    }
}