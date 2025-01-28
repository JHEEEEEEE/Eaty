package com.effort.presentation.core.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// ViewModel에서 List 형태의 값을 내보낼때 사용하는 함수
fun <T, R> DataResource<List<T>>.toUiStateList(
    transform: (T) -> R // 리스트 내부 요소 변환 함수
): UiState<List<R>> {
    return when (this) {
        is DataResource.Success -> UiState.Success(data.map { transform(it) }) // 리스트 내부 요소 변환
        is DataResource.Error -> UiState.Error(throwable)
        is DataResource.Loading -> UiState.Loading
    }
}

fun <T> handleCompletionState(
    state: MutableStateFlow<UiState<List<T>>>, // 상태 플로우
    cause: Throwable?,                        // 완료 원인 (에러 또는 null)
    clearOnCompletion: Boolean = true         // 성공 상태 초기화 여부 (기본값: true)
) {
    if (cause != null) { // 에러가 발생했을 경우
        state.value = UiState.Error(cause)
    } else if (clearOnCompletion && state.value is UiState.Loading) { // 로딩 상태라면 초기화
        state.value = UiState.Success(emptyList()) // 빈 리스트 반환
    }
}

fun <T> setLoadingState(state: MutableStateFlow<UiState<T>>) {
    state.value = UiState.Loading
}


// RestaurantFavoritesViewModel에 사용하는 Extenstion 함수
fun <T, U> validateCurrentUser(
    userState: StateFlow<UiState<U>>, // 사용자 상태를 일반화
    onErrorState: MutableStateFlow<UiState<T>>
): U? {
    val currentUser = (userState.value as? UiState.Success)?.data
    if (currentUser == null) {
        onErrorState.value = UiState.Error(Exception("User not logged in"))
    }
    return currentUser
}

fun <T> ViewModel.executeActionWithState(
    stateFlow: MutableStateFlow<UiState<T>>,
    action: suspend () -> DataResource<T>
) {
    viewModelScope.launch {
        stateFlow.value = UiState.Loading
        val result = action()
        stateFlow.value = when (result) {
            is DataResource.Success -> UiState.Success(result.data)
            is DataResource.Error -> UiState.Error(result.throwable)
            is DataResource.Loading -> UiState.Empty
        }
    }
}

fun <T, U> ViewModel.executeWithCurrentUser(
    userState: StateFlow<UiState<U>>, // 사용자 상태를 일반화
    onErrorState: MutableStateFlow<UiState<T>>,
    onSuccessState: MutableStateFlow<UiState<T>>,
    action: suspend (U) -> DataResource<T> // 이메일 대신 사용자 전체를 전달받도록 수정
) {
    val currentUser = validateCurrentUser(userState, onErrorState)
    if (currentUser != null) {
        executeActionWithState(onSuccessState) { action(currentUser) }
    }
}
