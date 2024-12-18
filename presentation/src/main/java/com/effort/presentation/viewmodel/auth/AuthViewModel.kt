@file:Suppress("DEPRECATION")

package com.effort.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import com.effort.domain.usecase.auth.SaveUserUseCase
import com.effort.presentation.UiState
import com.effort.presentation.model.auth.FirebaseUserModel
import com.effort.presentation.model.auth.toPresentation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticateUserUseCase: AuthenticateUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _authenticateState = MutableStateFlow<UiState<FirebaseUserModel>>(UiState.Empty)
    val authenticateState get() = _authenticateState.asStateFlow()

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    // Google 로그인 결과 처리
    fun handleSignInResult(account: GoogleSignInAccount) {
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
    }
}