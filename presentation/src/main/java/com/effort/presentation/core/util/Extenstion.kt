package com.effort.presentation.core.util

import com.effort.domain.DataResource
import com.effort.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow

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