package com.effort.feature.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentCommunityBinding
import com.effort.feature.databinding.FragmentRestaurantInfoBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment :
    BaseFragment<FragmentRestaurantInfoBinding>(FragmentRestaurantInfoBinding::inflate) {
    private lateinit var navController: NavController

    override fun initView() {
        //setupTabLayoutAndViewPager()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRestaurantInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 실행테스트 더미코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    /*private fun setupTabLayoutAndViewPager() {
        // NavController 연결
        try {
            navController = findNavController()  // 부모 Fragment의 NavController를 찾음
        } catch (e: Exception) {
            Log.e("CommunityFragment", "NavController is not found", e)
        }

        val tabLayout: TabLayout = binding.tabs
        val viewPager: ViewPager2 = binding.tabContent

        val communityDetailAdapter = CommunityDetailAdapter(this)
        viewPager.adapter = communityDetailAdapter

        // TabLayout과 ViewPager 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "정보"
                1 -> tab.text = "리뷰"
                2 -> tab.text = "날씨"
            }
        }.attach()
    }*/
}