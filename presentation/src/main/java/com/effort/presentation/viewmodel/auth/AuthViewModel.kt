@file:Suppress("DEPRECATION")

package com.effort.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.model.auth.FirebaseUser
import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import com.effort.domain.usecase.auth.SaveUserUseCase
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
    private val saveUserUseCase: SaveUserUseCase,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _authenticateState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val authenticateState get() = _authenticateState.asStateFlow()

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun handleSignInResult(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authenticateState.value = UiState.Loading // 로딩 상태 설정

                when (val dataResource = authenticateUserUseCase(account.idToken!!)) {
                    is DataResource.Success -> {
                        if (dataResource.data) {
                            // 인증 성공 처리
                            _authenticateState.value = UiState.Success(Unit)
                        } else {
                            // 인증 실패 처리
                            _authenticateState.value =
                                UiState.Error(Exception("Authentication failed"))
                        }
                    }

                    is DataResource.Error -> {
                        // 인증 중 에러 발생 처리
                        _authenticateState.value = UiState.Error(dataResource.throwable)
                    }

                    is DataResource.Loading -> {
                        // 로딩 상태 유지
                        _authenticateState.value = UiState.Loading
                    }
                }
            } catch (e: Exception) {
                _authenticateState.value = UiState.Error(e) // 예외 처리
            } finally {
                if (_authenticateState.value is UiState.Loading) {
                    _authenticateState.value = UiState.Empty // 상태 초기화
                }
            }
        }
    }
}




// Google 로그인 결과 처리
/*fun handleSignInResult(account: GoogleSignInAccount) {
    viewModelScope.launch {
        authenticateUserUseCase(account.idToken!!)
            .onStart {
                _authenticateState.value = UiState.Loading
            }
            .onCompletion {
                if (_authenticateState.value is UiState.Loading) {
                    _authenticateState.value = UiState.Empty
                }
            }
            .collectLatest { dataResource ->
                when (dataResource) {
                    is DataResource.Success -> {
                        val firebaseUser = dataResource.data
                        saveUserUseCase(firebaseUser)
                        _authenticateState.value =
                            UiState.Success(firebaseUser.toPresentation())
                    }

                    is DataResource.Error -> {
                        _authenticateState.value =
                            UiState.Error(dataResource.throwable)
                    }

                    is DataResource.Loading -> {
                        _authenticateState.value =
                            UiState.Loading
                    }
                }

            }
    }
}*/
