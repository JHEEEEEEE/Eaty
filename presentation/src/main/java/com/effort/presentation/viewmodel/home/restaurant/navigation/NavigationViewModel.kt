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

    fun fetchNavigationPath(start: NavigationPathModel, end: NavigationPathModel) {
        setLoadingState(_getNavigationPathState)

        viewModelScope.launch {
            try {
                val startData = start.toDomain()
                val endData = end.toDomain()

                // UseCase를 통해 데이터 요청
                val dataResource = getNavigationPathUseCase(startData, endData)

                // `toUiStateList()`를 사용하여 변환 간소화
                _getNavigationPathState.value = dataResource.toUiStateList { it.toPresentation() }

            } catch (e: Exception) {
                _getNavigationPathState.value = UiState.Error(e)
            }
        }
    }
}