package com.effort.feature.home.restaurant.detail.surrounding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemWeatherBinding
import com.effort.presentation.model.home.restaurant.detail.weather.WeatherModel

class WeatherCarouselAdapter :
    ListAdapter<WeatherModel, WeatherCarouselAdapter.WeatherViewHolder>(DiffCallback) {

    inner class WeatherViewHolder(private val binding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

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