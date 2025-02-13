package com.effort.presentation.viewmodel.home.restaurant

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RestaurantOverviewViewModel @Inject constructor() : ViewModel() {

    private val _title = MutableStateFlow("")
    val title get() = _title.asStateFlow()

    private val _location = MutableStateFlow<Pair<String, String>?>(null)
    val location get() = _location.asStateFlow()

    private val _region = MutableStateFlow("")
    val region get() = _region.asStateFlow()

    fun setTitle(newTitle: String) {
        if (newTitle.isNotEmpty()) {
            _title.value = newTitle
        } else {
            Log.e("SharedViewModel", "Title 값이 비어있습니다.")
        }
    }

    fun setLocation(latitude: String, longitude: String) {
        try {
            if (latitude.isNotBlank() && longitude.isNotBlank()) {
                _location.value = Pair(latitude, longitude)
                Log.d("SharedViewModel", "위치 설정 완료: 위도 $latitude, 경도 $longitude")
            }
        } catch (e: NumberFormatException) {
            Log.e("SharedViewModel", "위경도 변환 오류: ${e.message}")
        }
    }

    fun setRegion(region: String) {
        if (region.isNotEmpty()) {
            _region.value = region
        } else {
            Log.e("SharedViewModel", "Title 값이 비어있습니다.")
        }
    }
}