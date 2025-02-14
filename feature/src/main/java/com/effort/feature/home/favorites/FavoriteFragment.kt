package com.effort.feature.home.favorites

import android.os.Bundle
import android.util.Log
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
class FavoriteFragment : BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {

    private val viewModel: RestaurantFavoritesViewModel by viewModels()
    private lateinit var favoriteAdapter: RestaurantListAdapter

    override fun initView() {
        initRecyclerView()
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initRecyclerView() {
        // 찜 목록 어댑터
        favoriteAdapter = RestaurantListAdapter { restaurant ->
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToRestaurantDetailFragment(
                title = restaurant.title,
                lotNumberAddress = restaurant.lotNumberAddress,
                roadNameAddress = restaurant.roadNameAddress,
                distance = restaurant.distance,
                phoneNumber = restaurant.phoneNumber,
                placeUrl = restaurant.placeUrl,
                latitude = restaurant.latitude,
                longitude = restaurant.longitude
            )
            findNavController().navigate(action) // 찜 목록에서 상세로 이동
        }
        binding.recyclerviewRestaurant.apply {
            adapter = favoriteAdapter
        }
    }

    private fun observeViewModel() {
        observeStateLatest(
            stateFlow = viewModel.getFavoriteState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { favoriteData ->
            favoriteAdapter.submitList(favoriteData) // 찜 목록 업데이트
        }

        observeStateLatest(
            stateFlow = viewModel.userState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { user ->
            Log.d("RestaurantDetailFragment", "User updated: ${user?.email}")
            viewModel.fetchFavorites() // 찜 목록 불러오기
        }
    }
}