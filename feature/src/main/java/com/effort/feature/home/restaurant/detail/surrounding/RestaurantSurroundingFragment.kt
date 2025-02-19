package com.effort.feature.home.restaurant.detail.surrounding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
     * 주변 정보(날씨, 지하철역)를 표시하는 RecyclerView 설정
     */
    private fun setupRecyclerView() {
        weatherCarouselAdapter = WeatherCarouselAdapter()
        binding.recyclerviewWeather.apply {
            layoutManager = CarouselLayoutManager(
                MultiBrowseCarouselStrategy(), CarouselLayoutManager.HORIZONTAL
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
     * ViewModel에서 위치 정보를 감지하여 날씨 및 지하철역 데이터 요청
     * - SharedViewModel에서 위치 정보를 수집
     * - 위치 정보가 변경되면 날씨 및 지하철역 데이터를 다시 요청
     */
    private fun fetchLocationData() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.location.collectLatest { location ->
                location?.let { (latitude, longitude) ->
                    viewModel.fetchWeatherData(latitude, longitude)
                    viewModel.fetchSubwayStation(latitude, longitude)
                }
            }
        }
    }

    /**
     * ViewModel에서 날씨 및 지하철역 데이터를 감지하여 UI 업데이트
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
     * - 사용자가 특정 지하철역을 클릭하면 지도 앱에서 해당 위치를 탐색하도록 처리
     *
     * @param subwayModel 클릭된 지하철역 정보
     */
    private fun handleSubwayClick(subwayModel: SubwayModel) {
        val geoUri =
            "geo:${subwayModel.latitude},${subwayModel.longitude}?q=${subwayModel.placeName}"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(mapIntent)
    }
}