package com.effort.feature.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.core.util.updateChipStyle
import com.effort.feature.databinding.ItemFilterBinding
import com.effort.presentation.model.map.FilterModel

/**
 * 필터 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 필터를 선택하면 `onFilterSelected` 콜백을 통해 해당 필터 ID를 전달
 */
class FilterAdapter(
    private val onFilterSelected: (Int) -> Unit // Filter Id 전달
) : ListAdapter<FilterModel, FilterAdapter.FilterViewHolder>(DiffCallback) {

    inner class FilterViewHolder(private val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 필터 데이터를 UI에 바인딩
         * - 필터 이름을 설정하고 초기 선택 상태를 적용
         * - 클릭 시 선택 상태를 변경하고 스타일을 업데이트
         */
        fun bind(item: FilterModel) {
            with(binding.chipFilter) {
                text = item.displayName
                isChecked = item.isSelected

                updateChipStyle(this, isChecked) // 초기 스타일 적용

                setOnClickListener {
                    isChecked = !isChecked // 선택 상태 토글
                    updateChipStyle(this, isChecked) // 선택 상태에 따라 스타일 변경
                    onFilterSelected(item.id) // 선택된 필터 ID 전달
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
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 `id` 값을 가진 필터는 동일한 아이템으로 간주
         */
        override fun areItemsTheSame(oldItem: FilterModel, newItem: FilterModel): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FilterModel, newItem: FilterModel): Boolean = oldItem == newItem
    }
}