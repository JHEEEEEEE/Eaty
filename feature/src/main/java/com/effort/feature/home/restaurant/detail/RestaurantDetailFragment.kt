package com.effort.feature.home.restaurant.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.effort.feature.community.CommunityDetailAdapter
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.extractGuFromSeoulAddress
import com.effort.feature.databinding.FragmentRestaurantDetailBinding
import com.effort.presentation.viewmodel.home.SharedRestaurantViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RestaurantDetailFragment :
    BaseFragment<FragmentRestaurantDetailBinding>(FragmentRestaurantDetailBinding::inflate) {
    private lateinit var navController: NavController
    private val args: RestaurantDetailFragmentArgs by navArgs() // SafeArgs로 데이터 받기
    private val sharedViewModel: SharedRestaurantViewModel by activityViewModels()

    override fun initView() {
        setupTabLayoutAndViewPager()

        bindRestaurantDetails(args)

        // 데이터 공유 설정
        setupSharedViewModelData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 실행테스트 더미코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun setupTabLayoutAndViewPager() {
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

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "정보"
                1 -> tab.text = "리뷰"
                2 -> tab.text = "날씨"
            }
        }.attach()
    }

    private fun bindRestaurantDetails(args: RestaurantDetailFragmentArgs) {
        with(binding) {
            restaurantTitle.text = args.title
            restaurantLotNumberAddress.text = args.lotNumberAddress
            restaurantRoadNameAddress.text = "${args.distance}m"
            restaurantNumber.text = args.phoneNumber
            restaurantWebpage.text = args.placeUrl
        }
    }

    private fun setupSharedViewModelData() {
        with(sharedViewModel) {
            setTitle(args.title) // ViewModel에 title 저장
            setLocation(args.latitude, args.longitude)
            setRegion(extractGuFromSeoulAddress(args.lotNumberAddress))
        }
    }
}