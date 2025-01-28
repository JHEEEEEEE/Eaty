package com.effort.feature.home.restaurant.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.effort.feature.R
import com.effort.feature.community.CommunityDetailAdapter
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.extractGuDongFromSeoulAddress
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentRestaurantDetailBinding
import com.effort.presentation.UiState
import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.effort.presentation.viewmodel.home.restaurant.SharedRestaurantViewModel
import com.effort.presentation.viewmodel.home.restaurant.favorites.RestaurantFavoritesViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantDetailFragment :
    BaseFragment<FragmentRestaurantDetailBinding>(FragmentRestaurantDetailBinding::inflate) {
    private lateinit var navController: NavController
    private val args: RestaurantDetailFragmentArgs by navArgs() // SafeArgs로 데이터 받기
    private val restaurantFavoritesViewModel: RestaurantFavoritesViewModel by viewModels()
    private val sharedViewModel: SharedRestaurantViewModel by activityViewModels()

    override fun initView() {
        setupTabLayoutAndViewPager()

        bindRestaurantDetails(args)
        setupFavoriteButton() // 하트 버튼 초기화
        observeUserState() // 사용자 상태 관찰
        observeFavoriteCheckState() // 찜 여부 확인 상태 관찰
        observeAddFavoriteState()
        observeRemoveFavoriteState()

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
            Log.e("RestaurantDetailFragment", "NavController is not found", e)
        }

        val tabLayout: TabLayout = binding.tabs
        val viewPager: ViewPager2 = binding.tabContent

        val communityDetailAdapter = CommunityDetailAdapter(this)
        viewPager.adapter = communityDetailAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "정보"
                1 -> tab.text = "리뷰"
                2 -> tab.text = "주변"
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
            setRegion(extractGuDongFromSeoulAddress(args.lotNumberAddress))
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteButton.isSelected = isFavorite
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_heart_filled_favorites_24 else R.drawable.ic_heart_empty_24
        )
    }

    private fun setupFavoriteButton() {
        val restaurant = createRestaurantModel()

        // 버튼 클릭 리스너 설정
        binding.favoriteButton.setOnClickListener {
            if (!binding.favoriteButton.isSelected) {
                restaurantFavoritesViewModel.addRestaurantToFavorites(restaurant)
            } else {
                restaurantFavoritesViewModel.removeRestaurantFromFavorites(restaurant.title)
            }
        }
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            restaurantFavoritesViewModel.userState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val user = state.data
                        Log.d("RestaurantDetailFragment", "User updated: ${user?.email}")
                        restaurantFavoritesViewModel.checkIfRestaurantIsFavorite(args.title) // 사용자 업데이트 후 찜 여부 확인
                    }

                    is UiState.Loading -> {
                        Log.d("RestaurantDetailFragment", "Loading user data...")
                    }

                    is UiState.Error -> {
                        Log.e(
                            "RestaurantDetailFragment",
                            "Error loading user data: ${state.exception.message}"
                        )
                    }

                    is UiState.Empty -> {
                        Log.d("RestaurantDetailFragment", "User state is empty")
                    }
                }
            }
        }
    }

    private fun observeFavoriteCheckState() {
        viewLifecycleOwner.lifecycleScope.launch {
            restaurantFavoritesViewModel.isFavoriteState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        updateFavoriteButton(state.data)
                        Log.d("RestaurantDetailFragment", "Favorite state: ${state.data}")
                    }

                    is UiState.Loading -> Log.d(
                        "RestaurantDetailFragment",
                        "Loading favorite state..."
                    )

                    is UiState.Empty -> Log.d("RestaurantDetailFragment", "Favorite state is empty")
                    is UiState.Error -> Log.e(
                        "RestaurantDetailFragment",
                        "Error checking favorite state: ${state.exception.message}"
                    )
                }
            }
        }
    }

    private fun observeAddFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            restaurantFavoritesViewModel.addFavoriteState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.favoriteButton.isEnabled = true
                        updateFavoriteButton(true)
                        showToast("찜 목록에 추가되었습니다.")
                        Log.d("RestaurantDetailFragment", "Successfully added to favorites")
                    }

                    is UiState.Loading -> {
                        binding.favoriteButton.isEnabled = false
                        Log.d("RestaurantDetailFragment", "Adding to favorites...")
                    }

                    is UiState.Error -> {
                        binding.favoriteButton.isEnabled = true
                        showToast("오류 발생: ${state.exception.message}")
                        Log.e(
                            "RestaurantDetailFragment",
                            "Error adding to favorites: ${state.exception.message}"
                        )
                    }

                    is UiState.Empty -> Log.d(
                        "RestaurantDetailFragment",
                        "Add favorite state is empty"
                    )
                }
            }
        }
    }

    private fun observeRemoveFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            restaurantFavoritesViewModel.removeFavoriteState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.favoriteButton.isEnabled = true
                        updateFavoriteButton(false)
                        showToast("찜 목록에서 제거되었습니다.")
                        Log.d("RestaurantDetailFragment", "Successfully removed from favorites")
                    }

                    is UiState.Loading -> {
                        binding.favoriteButton.isEnabled = false
                        Log.d("RestaurantDetailFragment", "Removing from favorites...")
                    }

                    is UiState.Error -> {
                        binding.favoriteButton.isEnabled = true
                        showToast("오류 발생: ${state.exception.message}")
                        Log.e(
                            "RestaurantDetailFragment",
                            "Error removing from favorites: ${state.exception.message}"
                        )
                    }

                    is UiState.Empty -> Log.d(
                        "RestaurantDetailFragment",
                        "Remove favorite state is empty"
                    )
                }
            }
        }
    }

    private fun createRestaurantModel(): RestaurantModel {
        return RestaurantModel(
            title = args.title,
            lotNumberAddress = args.lotNumberAddress,
            roadNameAddress = args.roadNameAddress,
            phoneNumber = args.phoneNumber,
            placeUrl = args.placeUrl,
            distance = "",
            latitude = args.latitude,
            longitude = args.longitude
        )
    }
}
