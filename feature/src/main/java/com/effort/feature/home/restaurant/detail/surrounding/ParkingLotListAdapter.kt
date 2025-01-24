package com.effort.feature.home.restaurant.detail.surrounding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemParkinglotBinding
import com.effort.presentation.model.home.restaurant.detail.parkinglot.ParkingLotModel

class ParkingLotListAdapter :
    ListAdapter<ParkingLotModel, ParkingLotListAdapter.ParkingLotViewHolder>(DiffCallback) {

    class ParkingLotViewHolder(private val binding: ItemParkinglotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ParkingLotModel) {
            with(binding) {
                id.text = item.id
                name.text = item.name
                address.text = item.address
                latitude.text = item.latitude
                longitude.text = item.longitude
                capacity.text = "주차 구획 수: ${item.capacity}"
            }

            // 클릭 이벤트 (필요시 확장 가능)
            binding.root.setOnClickListener {
                Toast.makeText(it.context, "${item.name} 선택됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotViewHolder {
        val binding =
            ItemParkinglotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkingLotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkingLotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ParkingLotModel>() {
            override fun areItemsTheSame(
                oldItem: ParkingLotModel, newItem: ParkingLotModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ParkingLotModel, newItem: ParkingLotModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}