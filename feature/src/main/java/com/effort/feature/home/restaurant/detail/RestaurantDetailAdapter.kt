package com.effort.feature.home.restaurant.detail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.effort.feature.home.restaurant.detail.info.RestaurantInfoFragment
import com.effort.feature.home.restaurant.detail.review.RestaurantReviewFragment
import com.effort.feature.home.restaurant.detail.surrounding.RestaurantSurroundingFragment

/**
 * 식당 상세 화면에서 ViewPager2를 위한 FragmentStateAdapter
 * - 3개의 Fragment를 포함하여 식당 정보, 리뷰, 주변 정보를 제공
 * - `position` 값에 따라 적절한 Fragment를 반환
 */
class RestaurantDetailAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    /**
     * `position` 값에 따라 해당하는 Fragment를 반환
     * - `0` → 식당 정보 (`RestaurantInfoFragment`)
     * - `1` → 리뷰 (`RestaurantReviewFragment`)
     * - `2` → 주변 정보 (`RestaurantSurroundingFragment`)
     * - 유효하지 않은 값이 들어오면 `IllegalStateException` 발생
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RestaurantInfoFragment()
            1 -> RestaurantReviewFragment()
            2 -> RestaurantSurroundingFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
