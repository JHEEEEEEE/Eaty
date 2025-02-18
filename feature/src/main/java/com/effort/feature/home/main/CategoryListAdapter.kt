package com.effort.feature.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemCategoryBinding
import com.effort.presentation.model.home.CategoryModel

/**
 * 카테고리 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 카테고리를 선택하면 해당 카테고리 이름을 onClick 콜백을 통해 전달
 */
class CategoryListAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<CategoryModel, CategoryListAdapter.CategoryViewHolder>(diffCallback) {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 카테고리 데이터를 UI에 바인딩하고 클릭 이벤트를 설정
         */
        fun bind(item: CategoryModel) {
            binding.categoryName.text = item.title
            binding.categoryImage.setImageResource(item.imageResId)

            binding.root.setOnClickListener {
                onClick(item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 제목(title)을 가진 카테고리는 동일한 아이템으로 간주
         */
        val diffCallback = object : DiffUtil.ItemCallback<CategoryModel>() {
            override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}