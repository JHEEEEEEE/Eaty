package com.effort.feature.home.restaurant.detail.surrounding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentRestaurantSurroundingBinding
import com.effort.presentation.viewmodel.home.restaurant.SharedRestaurantViewModel
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.home.restaurant.detail.surrounding.RestaurantSurroundingViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantSurroundingFragment :
    BaseFragment<FragmentRestaurantSurroundingBinding>(FragmentRestaurantSurroundingBinding::inflate) {

    private val viewModel: RestaurantSurroundingViewModel by viewModels()
    private val sharedViewModel: SharedRestaurantViewModel by activityViewModels()
    private lateinit var parkingLotListAdapter: ParkingLotListAdapter
    private lateinit var weatherCarouselAdapter: WeatherCarouselAdapter


    override fun initView() {
        // RecyclerView 및 Adapter 초기화
        setupRecyclerView()
        //observeParkingLotsState()
        observeWeatherState()

        // SharedViewModel에서 위치 데이터를 수집하여 사용
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.location.collectLatest { location ->
                location?.let { (latitude, longitude) ->
                    Log.d("RestaurantSurroundingFragment", "위도: $latitude, 경도: $longitude")
                    //viewModel.fetchParkingLots(latitude, longitude)
                    viewModel.fetchWeatherData(latitude, longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantSurroundingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * RecyclerView 및 Adapter 초기화
     */
    private fun setupRecyclerView() {
        weatherCarouselAdapter = WeatherCarouselAdapter()
        binding.recyclerviewWeather.apply {
            layoutManager = CarouselLayoutManager(
                MultiBrowseCarouselStrategy(),
                CarouselLayoutManager.HORIZONTAL
            )
            adapter = weatherCarouselAdapter
            setHasFixedSize(true) // 성능 최적화
        }

        parkingLotListAdapter = ParkingLotListAdapter()
        binding.recyclerviewParkingLot.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = parkingLotListAdapter
            setHasFixedSize(true) // 성능 최적화
        }
    }

    /**
     * ViewModel 상태 관찰
     */
    private fun observeParkingLotsState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getParkingLotState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> progressIndicator.showLoading(true)
                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        parkingLotListAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            "에러: ${state.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiState.Empty -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(requireContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeWeatherState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWeatherState.collect { state ->
                when (state) {
                    is UiState.Loading -> progressIndicator.showLoading(true)
                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        weatherCarouselAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            "에러: ${state.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiState.Empty -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(requireContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}