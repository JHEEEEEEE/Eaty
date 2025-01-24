package com.effort.presentation.viewmodel.home.restaurant.detail.surrounding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.detail.parkinglot.GetParkingLotListUseCase
import com.effort.domain.usecase.home.restaurant.detail.weather.GetWeatherDataUseCase
import com.effort.presentation.UiState
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
    private val _getParkingLotState = MutableStateFlow<UiState<List<ParkingLotModel>>>(UiState.Empty)
    val getParkingLotState get() = _getParkingLotState.asStateFlow()

    // 날씨 데이터 가져오기
    fun fetchWeatherData(latitude: String, longitude: String) {
        _getWeatherState.value = UiState.Loading

        viewModelScope.launch {
            try {
                // UseCase를 통해 데이터 요청
                _getWeatherState.value = when (val dataResource = getWeatherDataUseCase(latitude, longitude)) {
                    is DataResource.Success -> {
                        val weatherData = dataResource.data.map { it.toPresentation() }
                        UiState.Success(weatherData)
                    }
                    is DataResource.Error -> {
                        UiState.Error(dataResource.throwable)
                    }
                    is DataResource.Loading -> {
                        UiState.Loading
                    }
                }
            } catch (e: Exception) {
                _getWeatherState.value = UiState.Error(e)
            }
        }
    }

    // 주차장 데이터 가져오기
    fun fetchParkingLots(latitude: String, longitude: String) {
        _getParkingLotState.value = UiState.Loading

        viewModelScope.launch {
            try {
                // UseCase를 통해 데이터 요청
                _getParkingLotState.value = when (val dataResource = getParkingLotListUseCase(latitude, longitude)) {
                    is DataResource.Success -> {
                        val parkingLots = dataResource.data.map { it.toPresentation() }
                        UiState.Success(parkingLots)
                    }
                    is DataResource.Error -> {
                        UiState.Error(dataResource.throwable)
                    }
                    is DataResource.Loading -> {
                        UiState.Loading
                    }
                }
            } catch (e: Exception) {
                _getParkingLotState.value = UiState.Error(e)
            }
        }
    }
}
