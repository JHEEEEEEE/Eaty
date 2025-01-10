package com.effort.feature.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.effort.feature.community.giveaway.GiveAwayFragment
import com.effort.feature.home.restaurant.detail.info.RestaurantInfoFragment
import com.effort.feature.home.restaurant.detail.surrounding.RestaurantSurroundingFragment

class CommunityDetailAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RestaurantInfoFragment()
            1 -> GiveAwayFragment()
            2 -> RestaurantSurroundingFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}