package com.effort.feature.home.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentRestaurantBinding
import com.effort.feature.home.restaurant.RestaurantListAdapter
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.home.restaurant.favorites.RestaurantFavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {
    private val viewModel: RestaurantFavoritesViewModel by viewModels()
    private lateinit var favoriteAdapter: RestaurantListAdapter


    override fun initView() {
        initRecyclerView()
        observeUserState()
        observeGetFavoriteState()
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

    private fun observeGetFavoriteState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFavoriteState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressIndicator.showLoading(true)
                        binding.recyclerviewRestaurant.visibility = View.GONE
                    }

                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        binding.recyclerviewRestaurant.visibility = View.VISIBLE
                        favoriteAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        binding.recyclerviewRestaurant.visibility = View.GONE
                    }

                    is UiState.Empty -> {
                        progressIndicator.showLoading(false)
                        binding.recyclerviewRestaurant.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val user = state.data
                        Log.d("RestaurantDetailFragment", "User updated: ${user?.email}")
                        viewModel.fetchFavorites() // 찜 목록 불러오기
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

}