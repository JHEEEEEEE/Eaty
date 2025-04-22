package com.effort.presentation.viewmodel.home.restaurant.detail.surrounding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effort.domain.DataResource
import com.effort.domain.usecase.home.restaurant.detail.subway.GetSubwayStationUseCase
import com.effort.domain.usecase.home.restaurant.detail.weather.GetWeatherDataUseCase
import com.effort.presentation.R
import com.effort.presentation.UiState
import com.effort.presentation.core.util.setLoadingState
import com.effort.presentation.core.util.toUiStateList
import com.effort.presentation.model.home.restaurant.detail.subway.SubwayModel
import com.effort.presentation.model.home.restaurant.detail.subway.toPresentation
import com.effort.presentation.model.home.restaurant.detail.weather.WeatherModel
import com.effort.presentation.model.home.restaurant.detail.weather.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantSurroundingViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getSubwayStationUseCase: GetSubwayStationUseCase,
) : ViewModel() {

    private val _getWeatherState = MutableStateFlow<UiState<List<WeatherModel>>>(UiState.Empty)
    val getWeatherState get() = _getWeatherState.asStateFlow()

    private val _getSubwayStationState = MutableStateFlow<UiState<List<SubwayModel>>>(UiState.Empty)
    val getSubwayStationState get() = _getSubwayStationState.asStateFlow()

    /**
     * 현재 위치(위도, 경도)를 기반으로 날씨 데이터를 가져온다.
     * - `latitude`, `longitude`를 기준으로 API 요청을 수행
     * - 날씨 데이터를 `WeatherModel`로 변환 후 UI 상태 업데이트
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    fun fetchWeatherData(latitude: String, longitude: String) {
        Timber.d("fetchWeatherData() 호출됨: 위도=$latitude, 경도=$longitude")
        setLoadingState(_getWeatherState)

        viewModelScope.launch {
            try {
                _getWeatherState.value =
                    when (val dataResource = getWeatherDataUseCase(latitude, longitude)) {
                        is DataResource.Success -> {
                            val weatherData = dataResource.data.map { it.toPresentation() }
                                .map { it.copy(weatherIcon = getWeatherIcon(it.id)) }

                            Timber.d("fetchWeatherData() 성공: 데이터 개수=${weatherData.size}")
                            UiState.Success(weatherData)
                        }
                        is DataResource.Error -> {
                            Timber.e(dataResource.throwable, "fetchWeatherData() 실패")
                            UiState.Error(dataResource.throwable)
                        }
                        is DataResource.Loading -> {
                            Timber.d("fetchWeatherData() 로딩 중")
                            UiState.Loading
                        }
                    }

                val dataResource = getWeatherDataUseCase(latitude, longitude)
                _getWeatherState.value = dataResource.toUiStateList { it.toPresentation() }
            } catch (e: Exception) {
                Timber.e(e, "fetchWeatherData() 예외 발생")
                _getWeatherState.value = UiState.Error(e)
            }
        }
    }

    /**
     * 현재 위치(위도, 경도)를 기반으로 주변 지하철역 정보를 가져온다.
     * - `latitude`, `longitude`를 기준으로 API 요청 수행
     * - 데이터가 존재하면 `UiState.Success`, 없으면 `UiState.Empty`
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    fun fetchSubwayStation(latitude: String, longitude: String) {
        Timber.d("fetchSubwayStation() 호출됨: 위도=$latitude, 경도=$longitude")

        setLoadingState(_getSubwayStationState)

        viewModelScope.launch {
            try {
                _getSubwayStationState.value =
                    when (val dataResource = getSubwayStationUseCase(latitude, longitude)) {
                        is DataResource.Success -> {
                            val subwayStation = dataResource.data.map { it.toPresentation() }
                            Timber.d("fetchSubwayStation() 성공: 받은 데이터 개수=${subwayStation.size}")
                            if (subwayStation.isEmpty()) {
                                Timber.d("fetchSubwayStation() 결과 없음")
                                UiState.Empty
                            } else {
                                UiState.Success(subwayStation)
                            }
                        }
                        is DataResource.Error -> {
                            Timber.e(dataResource.throwable, "fetchSubwayStation() API 실패")
                            UiState.Error(dataResource.throwable)
                        }
                        is DataResource.Loading -> {
                            Timber.d("fetchSubwayStation() 데이터 로딩 중...")
                            UiState.Loading
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "fetchSubwayStation() 예외 발생")
                _getSubwayStationState.value = UiState.Error(e)
            }
        }
    }

    /**
     * 날씨 상태 코드에 따라 아이콘을 반환한다.
     *
     * @param weatherId OpenWeatherMap 기준 날씨 ID
     * @return 해당 날씨에 맞는 아이콘 리소스 ID
     */
    private fun getWeatherIcon(weatherId: Int): Int {
        return when (weatherId) {
            in 200..232 -> R.drawable.ic_thunderstorm // 뇌우
            in 300..531 -> R.drawable.ic_rainy // 비
            in 600..622 -> R.drawable.ic_snowing // 눈
            in 701..781 -> R.drawable.ic_foggy // 안개 + 황사
            800 -> R.drawable.ic_sunny // 맑음
            in 801..802 -> R.drawable.ic_partly_cloudy // 구름 조금
            in 803..804 -> R.drawable.ic_cloudy // 흐림
            else -> R.drawable.ic_cloudy // 기본값: 흐림
        }
    }
}