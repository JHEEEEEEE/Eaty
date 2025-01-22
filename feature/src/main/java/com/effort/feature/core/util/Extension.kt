package com.effort.feature.core.util

import android.content.res.Resources.getSystem
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.chip.Chip
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
