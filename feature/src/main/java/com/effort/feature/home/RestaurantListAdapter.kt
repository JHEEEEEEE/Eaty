package com.effort.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemRestaurantBinding
import com.effort.presentation.model.home.RestaurantModel

class RestaurantListAdapter: ListAdapter<RestaurantModel, RestaurantListAdapter.RestaurantViewHolder>(diffCallback) {

    // 뷰 홀더 정의
    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RestaurantModel) {
            // 아이템 데이터 설정
            binding.restaurantName.text = item.title
            //binding.restaurantImage.setImageResource(item.imageResId) // 이미지 설정
        }
    }

    // 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding)
    }

    // 아이템 바인딩
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position)) // 아이템 바인딩
    }

    companion object {
        // DiffUtil을 사용한 아이템 변경 감지 최적화
        val diffCallback = object : DiffUtil.ItemCallback<RestaurantModel>() {
            override fun areItemsTheSame(oldItem: RestaurantModel, newItem: RestaurantModel): Boolean {
                return oldItem.title == newItem.title // ID가 같으면 동일한 아이템으로 간주
            }

            override fun areContentsTheSame(oldItem: RestaurantModel, newItem: RestaurantModel): Boolean {
                return oldItem == newItem // 내용이 동일하면 동일한 아이템으로 간주
            }
        }
    }
}