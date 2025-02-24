package com.effort.feature.home.restaurant.detail.surrounding

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemWeatherBinding
import com.effort.presentation.model.home.restaurant.detail.weather.WeatherModel

/**
 * 날씨 정보를 표시하는 RecyclerView Adapter
 * - 시간별 날씨 데이터를 가로 스크롤 방식으로 표시
 */
class WeatherCarouselAdapter :
    ListAdapter<WeatherModel, WeatherCarouselAdapter.WeatherViewHolder>(DiffCallback) {

    inner class WeatherViewHolder(private val binding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 날씨 데이터를 UI에 바인딩
         * - 아이콘, 시간, 기상 상태, 기온을 설정
         */
        @SuppressLint("DefaultLocale")
        fun bind(item: WeatherModel) {
            with(binding) {
                weatherIcon.setImageResource(item.weatherIcon)
                weatherTime.text = item.dateTime
                weatherCondition.text = item.condition
                weatherTemp.text = String.format("%.1f°C", item.temp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 dateTime을 가진 날씨 데이터는 동일한 아이템으로 간주
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<WeatherModel>() {
            override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
                return oldItem.dateTime == newItem.dateTime
            }

            override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}