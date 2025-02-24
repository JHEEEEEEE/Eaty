package com.effort.presentation.viewmodel.home.restaurant

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantOverviewViewModel @Inject constructor() : ViewModel() {

    private val _title = MutableStateFlow("")
    val title get() = _title.asStateFlow()

    private val _location = MutableStateFlow<Pair<String, String>?>(null)
    val location get() = _location.asStateFlow()

    private val _region = MutableStateFlow("")
    val region get() = _region.asStateFlow()

    /**
     * 식당 제목을 설정한다.
     * - 값이 비어있지 않은 경우에만 업데이트
     *
     * @param newTitle 새로운 제목 값
     */
    fun setTitle(newTitle: String) {
        if (newTitle.isNotEmpty()) {
            _title.value = newTitle
            Timber.d("setTitle() - 제목 설정 완료: $newTitle")
        } else {
            Timber.e("setTitle() - 실패: 빈 제목 입력")
        }
    }

    /**
     * 위경도를 설정한다.
     * - 빈 값이 아닌 경우에만 업데이트
     *
     * @param latitude 위도 값
     * @param longitude 경도 값
     */
    fun setLocation(latitude: String, longitude: String) {
        try {
            if (latitude.isNotBlank() && longitude.isNotBlank()) {
                _location.value = Pair(latitude, longitude)
                Timber.d("setLocation() - 위치 설정 완료: 위도 $latitude, 경도 $longitude")
            } else {
                Timber.e("setLocation() - 실패: 빈 위경도 값 입력")
            }
        } catch (e: NumberFormatException) {
            Timber.e(e, "setLocation() - 위경도 변환 오류")
        }
    }

    /**
     * 지역 정보를 설정한다.
     * - 값이 비어있지 않은 경우에만 업데이트
     *
     * @param region 새로운 지역 값
     */
    fun setRegion(region: String) {
        if (region.isNotEmpty()) {
            _region.value = region
            Timber.d("setRegion() - 지역 설정 완료: $region")
        } else {
            Timber.e("setRegion() - 실패: 빈 지역 값 입력")
        }
    }
}