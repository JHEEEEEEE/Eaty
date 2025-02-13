package com.effort.presentation.viewmodel.home.restaurant.detail.surrounding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.usecase.home.restaurant.detail.parkinglot.GetParkingLotListUseCase
import com.effort.domain.usecase.home.restaurant.detail.weather.GetWeatherDataUseCase
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.restaurant.detail.parkinglot.ParkingLotModel
import com.effort.presentation.model.home.restaurant.detail.parkinglot.toPresentation
import com.effort.presentation.model.home.restaurant.detail.weather.WeatherModel
import com.effort.presentation.model.home.restaurant.detail.weather.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantSurroundingViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getParkingLotListUseCase: GetParkingLotListUseCase
) : ViewModel() {

    // Weather 상태
    private val _getWeatherState = MutableStateFlow<UiState<List<WeatherModel>>>(UiState.Empty)
    val getWeatherState get() = _getWeatherState.asStateFlow()

    // Parking Lot 상태
    private val _getParkingLotState =
        MutableStateFlow<UiState<List<ParkingLotModel>>>(UiState.Empty)
    val getParkingLotState get() = _getParkingLotState.asStateFlow()

    // 날씨 데이터 가져오기
    fun fetchWeatherData(latitude: String, longitude: String) {
        setLoadingState(_getWeatherState)

        viewModelScope.launch {
            try {
                // UseCase를 통해 데이터 요청
                val dataResource = getWeatherDataUseCase(latitude, longitude)

                // `toUiStateList()`를 사용하여 변환 간소화
                _getWeatherState.value = dataResource.toUiStateList { it.toPresentation() }

            } catch (e: Exception) {
                _getWeatherState.value = UiState.Error(e)
            }
        }
    }

    // 주차장 데이터 가져오기
    fun fetchParkingLots(latitude: String, longitude: String) {
        setLoadingState(_getParkingLotState)

        viewModelScope.launch {
            try {
                // UseCase를 통해 데이터 요청
                val dataResource = getParkingLotListUseCase(latitude, longitude)

                // `toUiStateList()`를 사용하여 변환 간소화
                _getParkingLotState.value = dataResource.toUiStateList { it.toPresentation() }
            } catch (e: Exception) {
                _getParkingLotState.value = UiState.Error(e)
            }
        }
    }
}
