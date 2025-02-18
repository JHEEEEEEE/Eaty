package com.effort.presentation.viewmodel.home.restaurant.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.home.restaurant.navigation.GetNavigationPathUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.restaurant.navigation.NavigationPathModel
import com.effort.presentation.model.home.restaurant.navigation.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val getNavigationPathUseCase: GetNavigationPathUseCase
) : ViewModel() {

    private val _getNavigationPathState =
        MutableStateFlow<UiState<List<NavigationPathModel>>>(UiState.Empty)
    val getNavigationPathState get() = _getNavigationPathState.asStateFlow()

    /**
     * 출발지와 도착지를 기반으로 최적 경로를 요청한다.
     * - `getNavigationPathUseCase`를 호출하여 경로 데이터를 가져옴.
     * - `toUiStateList()`를 사용하여 UI에서 사용할 형태로 변환.
     *
     * @param start 출발지 정보
     * @param end 도착지 정보
     */
    fun fetchNavigationPath(start: NavigationPathModel, end: NavigationPathModel) {
        setLoadingState(_getNavigationPathState) // 로딩 상태 설정

        viewModelScope.launch {
            try {
                val startData = start.toDomain()
                val endData = end.toDomain()

                // UseCase를 통해 경로 데이터 요청
                val dataResource = getNavigationPathUseCase(startData, endData)

                // `toUiStateList()`를 사용하여 변환 후 상태 업데이트
                _getNavigationPathState.value = dataResource.toUiStateList { it.toPresentation() }

            } catch (e: Exception) {
                _getNavigationPathState.value = UiState.Error(e)
            }
        }
    }
}