package com.effort.presentation.viewmodel.home.restaurant.detail.surrounding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.detail.parkinglot.GetParkingLotListUseCase
import com.effort.domain.usecase.home.restaurant.detail.weather.GetWeatherDataUseCase
import com.effort.presentation.R
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
    private val _getParkingLotState =
        MutableStateFlow<UiState<List<ParkingLotModel>>>(UiState.Empty)
    val getParkingLotState get() = _getParkingLotState.asStateFlow()

    // 날씨 데이터 가져오기
    fun fetchWeatherData(latitude: String, longitude: String) {
        _getWeatherState.value = UiState.Loading

        viewModelScope.launch {
            try {
                // UseCase를 통해 데이터 요청
                _getWeatherState.value =
                    when (val dataResource = getWeatherDataUseCase(latitude, longitude)) {
                        is DataResource.Success -> {
                            val weatherData = dataResource.data.map { it.toPresentation() }
                                .map {
                                    it.copy(weatherIcon = getWeatherIcon(it.id))
                                }
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
                _getParkingLotState.value =
                    when (val dataResource = getParkingLotListUseCase(latitude, longitude)) {
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

    private fun getWeatherIcon(weatherId: Int): Int {
        return when (weatherId) {
            in 200..232 -> R.drawable.ic_thunderstorm  // 🌩️ 뇌우
            in 300..531 -> R.drawable.ic_rainy  // 🌧️ 비
            in 600..622 -> R.drawable.ic_snowing  // ❄️ 눈
            in 701..781 -> R.drawable.ic_foggy  // 🌫️ 안개 + 황사 (Fog + Dust)
            800 -> R.drawable.ic_sunny  // ☀️ 맑음
            in 801..802 -> R.drawable.ic_partly_cloudy  // 🌤️ 구름 조금 (partly cloudy)
            in 803..804 -> R.drawable.ic_cloudy  // ☁️ 흐림 (cloudy)
            else -> R.drawable.ic_cloudy  // ❓ 기본값: 흐림
        }
    }
}
