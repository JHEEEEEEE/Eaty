package com.effort.feature.home.restaurant.detail.surrounding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemSubwayBinding
import com.effort.presentation.model.home.restaurant.detail.subway.SubwayModel

class SubwayStationListAdapter(
    private val onItemClick: (SubwayModel) -> Unit
) : ListAdapter<SubwayModel, SubwayStationListAdapter.SubwayViewHolder>(DiffCallback) {

    inner class SubwayViewHolder(private val binding: ItemSubwayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubwayModel) {
            with(binding) {
                stationName.text = item.placeName
                stationDistance.text = item.distance
            }

            // 클릭 이벤트 (필요시 확장 가능)
            binding.root.setOnClickListener {
                onItemClick(item) // 클릭 이벤트 전달
                Toast.makeText(it.context, "${item.placeName} 선택됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubwayViewHolder {
        val binding = ItemSubwayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubwayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubwayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SubwayModel>() {
            override fun areItemsTheSame(
                oldItem: SubwayModel, newItem: SubwayModel
            ): Boolean {
                return oldItem.placeName == newItem.placeName
            }

            override fun areContentsTheSame(
                oldItem: SubwayModel, newItem: SubwayModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}