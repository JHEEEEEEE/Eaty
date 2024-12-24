@file:Suppress("DEPRECATION")

package com.effort.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import com.effort.domain.usecase.auth.CheckUserLoggedInUseCase
import com.effort.domain.usecase.auth.SignOutUseCase
import com.effort.presentation.UiState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            _authenticateState.value = UiState.Loading

            _authenticateState.value = when (val dataResource = checkUserLoggedInUseCase()) {
                is DataResource.Loading -> UiState.Loading
                is DataResource.Success -> {
                    if (dataResource.data) {
                        UiState.Success(Unit) // 성공 시 상태 업데이트
                    } else {
                        UiState.Error(Throwable("User not logged in"))
                    }
                }

                is DataResource.Error -> UiState.Error(dataResource.throwable)
            }
        }
    }


    fun handleSignInResult(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authenticateState.value = UiState.Loading // 로딩 상태 설정

                _authenticateState.value =
                    when (val dataResource = authenticateUserUseCase(account.idToken!!)) {
                        is DataResource.Success -> {
                            if (dataResource.data) {
                                // 인증 성공 처리
                                UiState.Success(Unit)
                            } else {
                                // 인증 실패 처리
                                UiState.Error(Exception("Authentication failed"))
                            }
                        }

                        is DataResource.Error -> {
                            // 인증 중 에러 발생 처리
                            UiState.Error(dataResource.throwable)
                        }

                        is DataResource.Loading -> {
                            // 로딩 상태 유지
                            UiState.Loading
                        }
                    }
            } catch (e: Exception) {
                UiState.Error(e) // 예외 처리
            } finally {
                if (_authenticateState.value is UiState.Loading) {
                    UiState.Empty // 상태 초기화
                }
            }
        }
    }


    fun signOut() {
        viewModelScope.launch {
            try {
                _signOutState.value = UiState.Loading

                _signOutState.value = when (val dataResource = signOutUseCase()) {
                    is DataResource.Success -> {
                        // Google 로그아웃 및 권한 해제 처리
                        googleSignInClient.signOut()
                        googleSignInClient.revokeAccess()

                        UiState.Success(true)
                    }

                    is DataResource.Error -> {
                        UiState.Error(dataResource.throwable)
                    }

                    is DataResource.Loading -> {
                        UiState.Loading
                    }
                }
            } catch (e: Exception) {
                UiState.Error(e)
            }
        }
    }
}