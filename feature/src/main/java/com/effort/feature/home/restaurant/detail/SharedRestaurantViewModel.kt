package com.effort.feature.home.restaurant.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedRestaurantViewModel @Inject constructor(): ViewModel() {

    private val _title = MutableStateFlow("")
    val title get() = _title.asStateFlow()

    fun setTitle(newTitle: String) {
        if (newTitle.isNotEmpty()) {
            _title.value = newTitle
        } else {
            Log.e("SharedViewModel", "Title 값이 비어있습니다.")
        }
    }
}