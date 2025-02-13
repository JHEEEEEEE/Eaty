package com.effort.feature.core.util

import android.content.res.Resources.getSystem
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.effort.feature.R
import com.effort.presentation.UiState
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


inline fun ImageView.load(
    data: Any?,
    glide: RequestManager = Glide.with(this),
    builder: RequestBuilder<*>.() -> Unit = {}
) {
    val requestBuilder = glide
        .load(data)
        .apply { builder() }

    requestBuilder.into(this)
}


fun Int.dp(): Int {
    val metrics = getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
        .toInt()
}


fun crossfade(enable: Boolean): DrawableTransitionOptions {
    return if (enable) {
        DrawableTransitionOptions.withCrossFade()
    } else {
        DrawableTransitionOptions().dontTransition()
    }
}

// ProgressBar의 visibility를 설정하는 확장 함수
fun ProgressBar.showLoading(isLoading: Boolean) {
    this.visibility = if (isLoading) View.VISIBLE else View.GONE
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun extractGuDongFromSeoulAddress(address: String): String {
    if (!address.contains("서울")) return "" // "서울"이 포함되지 않으면 빈 문자열 반환

    val regex = Regex("([가-힣]+구|[가-힣]+동)")
    return regex.findAll(address)
        .joinToString(" ") { it.value }
}

fun updateChipStyle(chip: Chip, isChecked: Boolean) {
    if (isChecked) {
        // 눌린 상태: 약간 움푹 들어간 것처럼 보이도록 크기 축소
        chip.elevation = 40f // 그림자 효과
        chip.scaleX = 0.85f
        chip.scaleY = 0.85f
    } else {
        // 기본 상태
        chip.elevation = 0f
        chip.scaleX = 1f
        chip.scaleY = 1f
    }
}

/**
 * Flow를 간단히 수집할 수 있는 유틸 함수
 */
fun <T> Fragment.collectFlow(flow: kotlinx.coroutines.flow.Flow<T>, collector: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        flow.collectLatest(collector)
    }
}

/**
 * [StateFlow]를 관찰하여 UI 상태를 처리하는 공통 확장 함수 (List<T> & 단일 값 모두 지원)
 */
fun <T> LifecycleOwner.observeState(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        stateFlow.collect { state -> // ✅ collect 사용 (모든 값 처리)
            when (state) {
                is UiState.Loading -> progressView.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressView.visibility = View.GONE
                    onSuccess(state.data)
                }
                is UiState.Error -> {
                    progressView.visibility = View.GONE
                    Toast.makeText(fragment.requireContext(), "에러: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Empty -> {
                    progressView.visibility = View.GONE
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

/**
 * [StateFlow]를 최신 데이터만 반영하며 관찰하는 공통 확장 함수
 */
fun <T> LifecycleOwner.observeStateLatest(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        stateFlow.collectLatest { state -> // ✅ collectLatest 사용 (최신 값만 반영)
            when (state) {
                is UiState.Loading -> progressView.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressView.visibility = View.GONE
                    onSuccess(state.data)
                }
                is UiState.Error -> {
                    progressView.visibility = View.GONE
                    Toast.makeText(fragment.requireContext(), "에러: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Empty -> {
                    progressView.visibility = View.GONE
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

fun <T> LifecycleOwner.observeStateLatestWithLifecycle(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) { // ✅ Lifecycle.STARTED에서만 collectLatest 실행
            stateFlow.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> progressView.visibility = View.VISIBLE
                    is UiState.Success -> {
                        progressView.visibility = View.GONE
                        onSuccess(state.data)
                    }
                    is UiState.Error -> {
                        progressView.visibility = View.GONE
                        Toast.makeText(fragment.requireContext(), "에러: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Empty -> {
                        progressView.visibility = View.GONE
                        Toast.makeText(fragment.requireContext(), fragment.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

fun <T> LifecycleOwner.observeStateContinuouslyWithLifecycle(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) { // ✅ Lifecycle.STARTED에서만 collect 실행
            stateFlow.collect { state -> // ✅ collect 사용 → 모든 데이터 순차적으로 반영
                when (state) {
                    is UiState.Loading -> progressView.visibility = View.VISIBLE
                    is UiState.Success -> {
                        progressView.visibility = View.GONE
                        onSuccess(state.data)
                    }
                    is UiState.Error -> {
                        progressView.visibility = View.GONE
                        Toast.makeText(fragment.requireContext(), "에러: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Empty -> {
                        progressView.visibility = View.GONE
                        Toast.makeText(fragment.requireContext(), fragment.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
