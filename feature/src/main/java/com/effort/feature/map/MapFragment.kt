package com.effort.feature.map

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentMapBinding
import com.effort.presentation.UiState
import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.effort.presentation.model.map.FilterModel
import com.effort.presentation.viewmodel.home.RestaurantViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMap.OnLocationChangeListener
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs


@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: RestaurantViewModel by viewModels()
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var navController: NavController
    private val markers = mutableListOf<Marker>() // 기존 마커를 관리할 리스트


    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var locationOverlay: LocationOverlay

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun initView() {
        setupMapFragment()
        initRecyclerView()
        setFilterList()
        observeSelectedFilter()
        setupFabClickListener()
        observeNewItemLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as MapFragment?
            ?: MapFragment.newInstance(
                NaverMapOptions()
                    .camera(
                        CameraPosition(
                            LatLng(37.5112, 127.0590),
                            14.0
                        )
                    )
                    .indoorEnabled(true)
                    .locationButtonEnabled(true)
            ).also {
                childFragmentManager.beginTransaction()
                    .replace(binding.map.id, it)
                    .commit()
            }

        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        initializeMap()

        viewModel.fetchRestaurants("")
    }

    private fun initializeMap() {
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        setOverlay()
        observeCameraInitialization()
        observeObtainRestaurantState()
    }

    private fun observeObtainRestaurantState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRestaurantState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> progressIndicator.showLoading(true)
                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        updateMapMarkers(state.data) // state.data가 List<RestaurantModel> 리스트 형태이다 (여기에서 list를 받아와서 restaurant.전체 데이터 넘기면 될듯)
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        showToast("Error: ${state.exception.message}")
                    }

                    is UiState.Empty -> progressIndicator.showLoading(false)
                }
            }
        }
    }

    private fun updateMapMarkers(restaurantList: List<RestaurantModel>) {
        if (!::naverMap.isInitialized) {
            Log.e("MapFragment", "NaverMap이 초기화되지 않았습니다.")
            return
        }

        val existingCoordinates = mutableListOf<LatLng>() // 기존 좌표 저장

        restaurantList.forEachIndexed { index, restaurant ->
            val originalLatLng = LatLng(restaurant.latitude.toDouble(), restaurant.longitude.toDouble())

            // 근사값 중복 여부 확인
            val adjustedLatLng = if (isCloseToExistingCoordinates(existingCoordinates, originalLatLng)) {
                // 근사값 중복된 경우 오차를 적용
                adjustMarkerPosition(originalLatLng.latitude, originalLatLng.longitude, index)
            } else {
                // 중복되지 않은 경우 원래 좌표 사용
                originalLatLng
            }

            // 현재 좌표를 기존 좌표 목록에 추가
            existingCoordinates.add(adjustedLatLng)

            // 마커 생성 및 추가
            Marker().apply {
                position = adjustedLatLng
                map = naverMap
                captionText = restaurant.title

                // 마커 클릭 리스너 설정
                setOnClickListener {
                    navigateToDetailFragment(restaurant)
                    true
                }

                markers.add(this) // 리스트에 추가
            }
        }
    }

    // 근사값으로 중복 여부 확인
    private fun isCloseToExistingCoordinates(existingCoordinates: List<LatLng>, target: LatLng): Boolean {
        val threshold = 0.0001 // 근사값 범위 (위도/경도 차이의 허용 범위)
        return existingCoordinates.any { coord ->
            abs(coord.latitude - target.latitude) <= threshold &&
                    abs(coord.longitude - target.longitude) <= threshold
        }
    }

    // 마커 위치를 약간 조정
    private fun adjustMarkerPosition(latitude: Double, longitude: Double, index: Int): LatLng {
        val offset = 0.0001 * (index % 5) // 중복된 마커 간 간격 조정
        return LatLng(latitude + offset, longitude + offset)
    }

    private fun observeCameraInitialization() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isCameraInitialized.collect { isInitialized ->
                if (!isInitialized) {
                    enableLocationTrackingOnce()
                }
            }
        }
    }

    // Fragment에서 LiveData 관찰
    private fun observeNewItemLiveData() {
        viewModel.newItemLiveData.observe(viewLifecycleOwner) { message ->
            // isLastPage가 true일 때 "모든 데이터가 출력되었습니다." 메시지 출력
            if (viewModel.isLastPage) {
                showToast("모든 데이터가 출력되었습니다.")
            } else {
                // 새로 로드된 데이터가 있을 경우 메시지 출력
                showToast(message)
            }
        }
    }

    private fun enableLocationTrackingOnce() {
        val locationChangeListener = createLocationChangeListener()
        naverMap.addOnLocationChangeListener(locationChangeListener)
    }

    private fun createLocationChangeListener(): OnLocationChangeListener {
        return object : OnLocationChangeListener {
            override fun onLocationChange(location: Location) {
                location.let {
                    moveCameraToLocation(it)
                    Log.d("MapFragment", "현재 위치: ${it.latitude}, ${it.longitude}")
                    naverMap.locationTrackingMode = LocationTrackingMode.None
                    naverMap.removeOnLocationChangeListener(this)
                    viewModel.toggleCameraInitialization()
                }
            }
        }
    }

    private fun moveCameraToLocation(location: Location) {
        val cameraUpdate = CameraUpdate.toCameraPosition(
            CameraPosition(
                LatLng(location.latitude, location.longitude),
                15.0
            )
        ).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun setOverlay() {
        locationOverlay = naverMap.locationOverlay.apply { isVisible = true }
    }

    private fun initRecyclerView() {
        filterAdapter = FilterAdapter { selectedFilter ->
            viewModel.selectFilter(selectedFilter)
        }
        binding.recyclerViewFilter.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }
    }

    private fun observeSelectedFilter() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedFilter.collectLatest { selectedFilter ->
                // 선택된 필터가 변경되었을 때 기존 마커 제거
                clearMarkers()

                // 필터에 따라 마커를 업데이트 (데이터는 이미 ViewModel에서 로드됨)
                selectedFilter?.let { filter ->
                    Log.d("MapFragment", "선택된 필터: ${filter.query}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filters.collectLatest { filters ->
                filterAdapter.submitList(filters) // 필터 목록 갱신
            }
        }
    }

    private fun clearMarkers() {
        markers.forEach { it.map = null } // 기존 마커를 지도에서 제거
        markers.clear() // 리스트 초기화
    }

    private fun setupFabClickListener() {
        binding.fab.setOnClickListener {
            val selectedFilter = viewModel.selectedFilter.value
            if (selectedFilter != null) {
                viewModel.fetchRestaurants(selectedFilter.query, loadMore = true)
            } else {
                showToast("필터를 선택해주세요.")
            }
        }
    }

    private fun setFilterList() {
        viewModel.setFilters(
            listOf(
                FilterModel(1, "한식", "한식"),
                FilterModel(2, "중식", "중식"),
                FilterModel(3, "양식", "양식"),
                FilterModel(4, "일식", "일식"),
                FilterModel(5, "아시아음식", "아시아음식"),
                FilterModel(6, "카페", "카페"),
                FilterModel(7, "베이커리", "베이커리"),
            )
        )
    }

    // DetailFragment로 이동하는 함수
    private fun navigateToDetailFragment(restaurant: RestaurantModel) {
        navController = findNavController()

        val action = MapFragmentDirections.actionMapFragmentToRestaurantDetailFragment(
            title = restaurant.title,
            lotNumberAddress = restaurant.lotNumberAddress,
            roadNameAddress = restaurant.roadNameAddress,
            distance = restaurant.distance,
            phoneNumber = restaurant.phoneNumber,
            placeUrl = restaurant.placeUrl,
            latitude = restaurant.latitude,
            longitude = restaurant.longitude
        )
        navController.navigate(action)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.toggleCameraInitialization()
    }
}