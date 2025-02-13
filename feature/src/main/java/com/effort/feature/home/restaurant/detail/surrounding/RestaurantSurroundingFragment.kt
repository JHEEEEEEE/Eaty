package com.effort.feature.home.restaurant.detail.surrounding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeState
import com.effort.feature.core.util.observeStateLatest
import com.effort.feature.databinding.FragmentRestaurantSurroundingBinding
import com.effort.presentation.model.home.restaurant.detail.subway.SubwayModel
import com.effort.presentation.viewmodel.home.restaurant.RestaurantOverviewViewModel
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
    private val sharedViewModel: RestaurantOverviewViewModel by activityViewModels()
    private lateinit var subwayStationListAdapter: SubwayStationListAdapter
    private lateinit var weatherCarouselAdapter: WeatherCarouselAdapter

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

    override fun initView() {
        setupRecyclerView()
        observeViewModel()
        fetchLocationData()
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
            setHasFixedSize(true)
        }

        subwayStationListAdapter = SubwayStationListAdapter { subwayModel ->
            handleSubwayClick(subwayModel)
        }
        binding.recyclerviewSubway.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = subwayStationListAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * ViewModel에서 위치 정보를 가져와 데이터 요청
     */
    private fun fetchLocationData() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.location.collectLatest { location ->
                location?.let { (latitude, longitude) ->
                    Log.d("RestaurantSurroundingFragment", "위도: $latitude, 경도: $longitude")

                    viewModel.fetchWeatherData(latitude, longitude)
                    viewModel.fetchSubwayStation(latitude, longitude)
                }
            }
        }
    }

    /**
     * ViewModel 상태 관찰 (Weather & Subway)
     */
    private fun observeViewModel() {
        observeState(
            stateFlow = viewModel.getWeatherState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { weatherData ->
            weatherCarouselAdapter.submitList(weatherData)
        }

        observeStateLatest(
            stateFlow = viewModel.getSubwayStationState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { subwayData ->
            subwayStationListAdapter.submitList(subwayData)
        }
    }

    /**
     * 지하철역 클릭 시 지도 앱으로 연결
     */
    private fun handleSubwayClick(subwayModel: SubwayModel) {
        val geoUri =
            "geo:${subwayModel.latitude},${subwayModel.longitude}?q=${subwayModel.placeName}"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(mapIntent)
    }
}