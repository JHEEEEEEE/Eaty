package com.effort.feature.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemCategoryBinding
import com.effort.presentation.model.category.CategoryModel

class CategoryListAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<CategoryModel, CategoryListAdapter.CategoryViewHolder>(diffCallback) {

    // 뷰 홀더 클래스 정의
    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryModel) {
            with(binding) {
                categoryName.text = item.title
                categoryImage.setImageResource(item.imageResId)

                root.setOnClickListener {
                    onClick(item.title)
                }
            }
        }
    }

    // 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding) // 올바른 타입 반환
    }

    // 아이템 바인딩
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position) // 아이템 가져오기
        holder.bind(item) // 뷰 홀더 바인딩 호출
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<CategoryModel>() {
            override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: CategoryModel,
                newItem: CategoryModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}