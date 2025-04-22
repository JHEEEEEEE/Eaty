package com.effort.feature.home.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatest
import com.effort.feature.databinding.FragmentRestaurantBinding
import com.effort.feature.home.restaurant.RestaurantListAdapter
import com.effort.presentation.viewmodel.home.restaurant.favorites.RestaurantFavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment :
    BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {

    private val viewModel: RestaurantFavoritesViewModel by viewModels()
    private lateinit var favoriteAdapter: RestaurantListAdapter

    override fun initView() {
        initRecyclerView()
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * 찜한 식당 목록을 표시하는 RecyclerView 초기화
     * - 클릭 시 상세 화면으로 이동
     */
    private fun initRecyclerView() {
        favoriteAdapter = RestaurantListAdapter { restaurant ->
            findNavController().navigate(
                FavoriteFragmentDirections.actionFavoriteFragmentToRestaurantDetailFragment(
                    title = restaurant.title,
                    lotNumberAddress = restaurant.lotNumberAddress,
                    roadNameAddress = restaurant.roadNameAddress,
                    distance = restaurant.distance,
                    phoneNumber = restaurant.phoneNumber,
                    placeUrl = restaurant.placeUrl,
                    latitude = restaurant.latitude,
                    longitude = restaurant.longitude
                )
            )
        }
        binding.recyclerviewRestaurant.adapter = favoriteAdapter
    }

    /**
     * 찜 목록과 사용자 정보를 관찰하여 UI 업데이트
     */
    private fun observeViewModel() {
        observeStateLatest(
            stateFlow = viewModel.getFavoriteState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { favoriteAdapter.submitList(it) } // 찜 목록 변경 시 RecyclerView 업데이트

        observeStateLatest(
            stateFlow = viewModel.userState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) {
            viewModel.fetchFavorites() // 사용자 정보 변경 시 찜 목록 새로고침
        }
    }
}