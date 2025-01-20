package com.effort.feature.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.core.util.updateChipStyle
import com.effort.feature.databinding.ItemFilterBinding
import com.effort.presentation.model.map.FilterModel

class FilterAdapter(
    private val onFilterSelected: (Int) -> Unit // Filter Id 전달
) : ListAdapter<FilterModel, FilterAdapter.FilterViewHolder>(DiffCallback) {

    inner class FilterViewHolder(private val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilterModel) {
            with(binding.chipFilter) {
                text = item.displayName
                isChecked = item.isSelected

                // 초기 상태에 따라 스타일 적용
                updateChipStyle(this, isChecked)

                setOnClickListener {
                    // 선택 상태 토글
                    isChecked = !isChecked
                    // 선택 상태에 따라 스타일 업데이트
                    updateChipStyle(this, isChecked)
                    // 클릭 이벤트 전달
                    onFilterSelected(item.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FilterModel>() {
        override fun areItemsTheSame(oldItem: FilterModel, newItem: FilterModel): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FilterModel, newItem: FilterModel): Boolean = oldItem == newItem
    }
}