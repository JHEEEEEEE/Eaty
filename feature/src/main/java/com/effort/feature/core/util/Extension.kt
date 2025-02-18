package com.effort.feature.core.util

import android.app.Activity
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

// ImageView에 Glide를 활용하여 이미지를 로드하는 확장 함수
inline fun ImageView.load(
    data: Any?,
    glide: RequestManager = Glide.with(this),
    builder: RequestBuilder<*>.() -> Unit = {}
) {
    glide.load(data).apply(builder).into(this)
}

// dp 값을 픽셀로 변환하는 확장 함수
fun Int.dp(): Int {
    val metrics = getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

// Glide의 크로스페이드 애니메이션을 활성화하거나 비활성화하는 함수
fun crossfade(enable: Boolean): DrawableTransitionOptions {
    return if (enable) {
        DrawableTransitionOptions.withCrossFade()
    } else {
        DrawableTransitionOptions().dontTransition()
    }
}

// ProgressBar의 가시성을 토글하는 확장 함수
fun ProgressBar.showLoading(isLoading: Boolean) {
    this.visibility = if (isLoading) View.VISIBLE else View.GONE
}

// Fragment에서 Toast 메시지를 간편하게 표시하는 확장 함수
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

// 서울 주소에서 "구" 또는 "동" 정보를 추출하는 함수
fun extractGuDongFromSeoulAddress(address: String): String {
    if (!address.contains("서울")) return "" // 서울이 포함되지 않은 주소는 빈 문자열 반환

    val regex = Regex("([가-힣]+구|[가-힣]+동)")
    return regex.findAll(address).joinToString(" ") { it.value }
}

// Chip의 스타일을 업데이트하는 함수 (선택 상태에 따라 크기와 그림자 변경)
fun updateChipStyle(chip: Chip, isChecked: Boolean) {
    if (isChecked) {
        chip.elevation = 40f
        chip.scaleX = 0.85f
        chip.scaleY = 0.85f
    } else {
        chip.elevation = 0f
        chip.scaleX = 1f
        chip.scaleY = 1f
    }
}

/**
 * Flow를 간편하게 수집하는 확장 함수
 */
fun <T> Fragment.collectFlow(flow: kotlinx.coroutines.flow.Flow<T>, collector: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        flow.collectLatest(collector)
    }
}

/**
 * [StateFlow]를 관찰하여 UI 상태를 처리하는 확장 함수
 * - 모든 값을 순차적으로 반영
 */
fun <T> LifecycleOwner.observeState(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        stateFlow.collect { state ->
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
 * [StateFlow]를 최신 데이터만 반영하며 관찰하는 확장 함수
 */
fun <T> LifecycleOwner.observeStateLatest(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
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

/**
 * [StateFlow]를 Fragment에서 Lifecycle을 준수하며 최신 데이터만 반영하는 확장 함수
 */
fun <T> LifecycleOwner.observeStateLatestWithLifecycleOnFragment(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
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

/**
 * [StateFlow]를 Lifecycle을 준수하며 지속적으로 관찰하는 확장 함수
 */
fun <T> LifecycleOwner.observeStateContinuouslyWithLifecycle(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    fragment: Fragment,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            stateFlow.collect { state ->
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

/**
 * [StateFlow]를 Activity에서 Lifecycle을 준수하며 최신 데이터만 반영하는 확장 함수
 */
fun <T> LifecycleOwner.observeStateLatestWithLifecycleOnActivity(
    stateFlow: StateFlow<UiState<T>>,
    progressView: View,
    activity: Activity,
    onSuccess: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            stateFlow.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> progressView.visibility = View.VISIBLE
                    is UiState.Success -> {
                        progressView.visibility = View.GONE
                        onSuccess(state.data)
                    }
                    is UiState.Error -> {
                        progressView.visibility = View.GONE
                        Toast.makeText(activity, "에러: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Empty -> {
                        progressView.visibility = View.GONE
                        Toast.makeText(activity, activity.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
/**
 * Success이외의 동작에 로직이 있을때 사용하는 [StateFlow]를 최신 데이터만 반영하며 관찰하는 확장 함수
 */

fun <T> LifecycleOwner.observeStateLatestForOtherLogic(
    stateFlow: StateFlow<UiState<T>>,
    fragment: Fragment,
    onSuccess: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onLoading: (() -> Unit)? = null,
    onEmpty: (() -> Unit)? = null
) {
    lifecycleScope.launch {
        stateFlow.collectLatest { state ->
            when (state) {
                is UiState.Success -> onSuccess(state.data)
                is UiState.Error -> {
                    onError?.invoke(state.exception) ?: Toast.makeText(
                        fragment.requireContext(),
                        "에러: ${state.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is UiState.Loading -> onLoading?.invoke()
                is UiState.Empty -> onEmpty?.invoke()
            }
        }
    }
}

