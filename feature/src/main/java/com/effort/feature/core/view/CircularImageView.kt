package com.effort.feature.core.view

import android.content.Context
import android.content.res.TypedArray
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.setPadding
import com.effort.feature.R
import com.effort.feature.core.util.crossfade
import com.effort.feature.core.util.dp
import com.effort.feature.core.util.load
import com.effort.feature.databinding.ViewCircularImageBinding


class CircularImageView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
        getAttrs(attrs, defStyle)
    }

    private lateinit var binding: ViewCircularImageBinding

    private fun initView() {
        binding = ViewCircularImageBinding.inflate(LayoutInflater.from(context), this, true)
        binding.imageview.clipToOutline = true
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView)
        setTypedArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyle, 0)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val srcId = typedArray.getResourceId(R.styleable.CircularImageView_android_src, 0)
        if (srcId != 0) {
            binding.imageview.setImageResource(srcId)
        }
        val padding = typedArray.getInt(R.styleable.CircularImageView_image_padding, 0)
        binding.imageview.setPadding(padding.dp())
        typedArray.recycle()
    }

    fun setImageUrl(imageUrl: Any) {
        when (imageUrl) {
            is String -> binding.imageview.load(imageUrl) {
                crossfade(true)
            }

            is Int -> binding.imageview.setImageResource(imageUrl)
            else -> throw IllegalArgumentException("Unsupported type for setImageUrl")
        }
    }

    fun setImageUri(imageUri: Uri?) {
        binding.imageview.load(imageUri) {
            crossfade(true)
        }
    }

}