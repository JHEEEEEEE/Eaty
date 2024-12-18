package com.effort.presentation

sealed class UiState<out T> {
    data object Empty : UiState<Nothing>() // 데이터가 없는 초기 상태
    data object Loading : UiState<Nothing>() // 로딩 중 상태
    data class Success<T>(val data: T) : UiState<T>() // 성공적으로 데이터가 로드된 상태
    data class Error(val exception: Throwable) : UiState<Nothing>() // 에러 상태

    /*// 리스트 데이터를 처리하는 Helper 함수
    companion object {
        fun <T> successList(data: List<T>): UiState<List<T>> = Success(data)
    }*/
}

