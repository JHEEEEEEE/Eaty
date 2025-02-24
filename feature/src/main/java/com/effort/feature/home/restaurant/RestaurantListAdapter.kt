package com.effort.feature.home.restaurant

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemRestaurantBinding
import com.effort.presentation.model.home.restaurant.RestaurantModel

/**
 * 식당 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 식당을 선택하면 `onItemClick` 콜백을 통해 해당 식당 정보를 전달
 */
class RestaurantListAdapter(
    private val onItemClick: (RestaurantModel) -> Unit // 클릭 이벤트를 NavDirections로 반환
) : ListAdapter<RestaurantModel, RestaurantListAdapter.RestaurantViewHolder>(diffCallback) {

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 식당 데이터를 UI에 바인딩
         * - 식당 이름, 주소, 거리, 전화번호, URL 정보를 설정
         * - 클릭 시 `onItemClick` 콜백을 호출
         */
        @SuppressLint("SetTextI18n")
        fun bind(item: RestaurantModel) {
            with(binding) {
                name.text = item.title
                lotNumberAddress.text = item.lotNumberAddress
                roadNameAddress.text = item.roadNameAddress
                distance.text = "${item.distance}m"
                phoneNumber.text = item.phoneNumber
                placeUrl.text = item.placeUrl

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 title(식당명)을 가진 경우 동일한 아이템으로 간주
         */
        val diffCallback = object : DiffUtil.ItemCallback<RestaurantModel>() {
            override fun areItemsTheSame(
                oldItem: RestaurantModel, newItem: RestaurantModel
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: RestaurantModel, newItem: RestaurantModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}