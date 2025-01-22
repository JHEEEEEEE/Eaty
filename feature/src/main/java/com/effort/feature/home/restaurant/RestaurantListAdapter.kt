package com.effort.feature.home.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemRestaurantBinding
import com.effort.presentation.model.home.restaurant.RestaurantModel

class RestaurantListAdapter :
    ListAdapter<RestaurantModel, RestaurantListAdapter.RestaurantViewHolder>(
        diffCallback
    ) {

    // 뷰 홀더 정의
    class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RestaurantModel) {
            // 아이템 데이터 설정

            with(binding) {
                name.text = item.title
                lotNumberAddress.text = item.lotNumberAddress
                roadNameAddress.text = item.roadNameAddress
                distance.text = "${item.distance}m"
                phoneNumber.text = item.phoneNumber
                placeUrl.text = item.placeUrl

                // 클릭 이벤트 추가
                root.setOnClickListener {
                    val action =
                        RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantDetailFragment(
                            title = item.title,
                            lotNumberAddress = item.lotNumberAddress,
                            roadNameAddress = item.roadNameAddress,
                            distance = item.distance,
                            phoneNumber = item.phoneNumber,
                            placeUrl = item.placeUrl,
                            latitude = item.latitude,
                            longitude = item.longitude
                        )
                    it.findNavController().navigate(action)
                }
            }
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
            override fun areItemsTheSame(
                oldItem: RestaurantModel,
                newItem: RestaurantModel
            ): Boolean {
                return oldItem.title == newItem.title // ID가 같으면 동일한 아이템으로 간주
            }

            override fun areContentsTheSame(
                oldItem: RestaurantModel,
                newItem: RestaurantModel
            ): Boolean {
                return oldItem == newItem // 내용이 동일하면 동일한 아이템으로 간주
            }
        }
    }
}