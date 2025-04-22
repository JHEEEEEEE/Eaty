package com.effort.feature.home.restaurant.detail.surrounding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemSubwayBinding
import com.effort.presentation.model.home.restaurant.detail.subway.SubwayModel

/**
 * 지하철역 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 지하철역을 선택하면 onItemClick 콜백을 통해 해당 역 정보를 전달
 */
class SubwayStationListAdapter(
    private val onItemClick: (SubwayModel) -> Unit
) : ListAdapter<SubwayModel, SubwayStationListAdapter.SubwayViewHolder>(DiffCallback) {

    inner class SubwayViewHolder(private val binding: ItemSubwayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 지하철역 데이터를 UI에 바인딩하고 클릭 이벤트를 설정
         */
        fun bind(item: SubwayModel) {
            with(binding) {
                stationName.text = item.placeName
                stationDistance.text = item.distance
            }

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
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 placeName을 가진 지하철역은 동일한 아이템으로 간주
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<SubwayModel>() {
            override fun areItemsTheSame(oldItem: SubwayModel, newItem: SubwayModel): Boolean {
                return oldItem.placeName == newItem.placeName
            }

            override fun areContentsTheSame(oldItem: SubwayModel, newItem: SubwayModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}