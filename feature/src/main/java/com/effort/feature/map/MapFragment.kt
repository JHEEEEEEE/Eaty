package com.effort.feature.map

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.collectFlow
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
import kotlin.math.abs


@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate),
    OnMapReadyCallback {

    // ViewModel을 Hilt를 통해 주입받음
    private val viewModel: RestaurantViewModel by viewModels()

    // 필터 리스트 어댑터
    private lateinit var filterAdapter: FilterAdapter

    // 네비게이션 컨트롤러
    private lateinit var navController: NavController

    // 지도에 표시된 마커를 관리하기 위한 리스트
    private val markers = mutableListOf<Marker>()

    // 위치 추적 소스와 지도 객체
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    // 현재 위치를 표시하는 오버레이
    private lateinit var locationOverlay: LocationOverlay

    companion object {
        // 위치 권한 요청 코드
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    /**
     * 초기 UI 설정을 담당하는 메서드
     */
    override fun initView() {
        setupMapFragment() // 네이버 지도 초기화
        initRecyclerView() // 필터 리스트 RecyclerView 초기화
        setFilterList() // 필터 데이터 설정
        setupFabClickListener() // FAB 버튼 클릭 리스너 설정
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView() // 초기화 로직 실행
    }

    /**
     * 네이버 지도(MapFragment)를 초기화
     */
    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as MapFragment?
            ?: MapFragment.newInstance(
                NaverMapOptions()
                    .camera(
                        CameraPosition(
                            LatLng(37.5112, 127.0590), // 초기 카메라 위치 (서울 강남)
                            14.0 // 줌 레벨
                        )
                    )
                    .indoorEnabled(true) // 실내 지도 활성화
                    .locationButtonEnabled(true) // 위치 버튼 활성화
            ).also {
                childFragmentManager.beginTransaction()
                    .replace(binding.map.id, it)
                    .commit()
            }

        mapFragment.getMapAsync(this) // 지도가 준비되었을 때 콜백 호출
    }

    /**
     * 네이버 지도 준비 완료 시 호출되는 콜백
     */
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap // 네이버 지도 객체 저장
        initializeMap() // 지도 초기화 로직 실행

        // 네이버 지도가 준비된 후 ViewModel 상태 관찰 시작
        observeViewModel()

        viewModel.fetchRestaurants("") // 초기 레스토랑 데이터 로드
    }

    /**
     * 지도 초기화: 위치 소스 설정, 오버레이 및 상태 관찰
     */
    private fun initializeMap() {
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE) // 위치 소스 설정
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow // 위치 추적 모드 활성화
        setOverlay() // 현재 위치 오버레이 설정
    }

    /**
     * ViewModel 상태를 관찰
     */
    private fun observeViewModel() {
        // 레스토랑 데이터 상태 관찰
        collectFlow(viewModel.getRestaurantState) { state ->
            handleRestaurantState(state)
        }

        // 카메라 초기화 상태 관찰
        collectFlow(viewModel.isCameraInitialized) { isInitialized ->
            if (!isInitialized) {
                enableLocationTrackingOnce()
            }
        }

        // 필터 선택 상태 관찰
        collectFlow(viewModel.selectedFilter) { selectedFilter ->
            handleFilterChange(selectedFilter)
        }

        // 필터 리스트 관찰
        collectFlow(viewModel.filters) { filters ->
            filterAdapter.submitList(filters)
        }

        // 새로운 아이템 관찰
        viewModel.newItemLiveData.observe(viewLifecycleOwner) { message ->
            if (viewModel.isLastPage) {
                showToast("모든 데이터가 출력되었습니다.")
            } else {
                showToast(message)
            }
        }
    }

    /**
     * 레스토랑 상태를 처리
     */
    private fun handleRestaurantState(state: UiState<List<RestaurantModel>>) {
        val progressIndicator = binding.progressCircular.progressBar

        when (state) {
            is UiState.Loading -> progressIndicator.showLoading(true)
            is UiState.Success -> {
                progressIndicator.showLoading(false)
                updateMapMarkers(state.data) // 지도에 마커 업데이트
            }
            is UiState.Error -> {
                progressIndicator.showLoading(false)
                showToast("Error: ${state.exception.message}")
            }
            is UiState.Empty -> progressIndicator.showLoading(false)
        }
    }

    /**
     * 필터 변경 처리
     */
    private fun handleFilterChange(selectedFilter: FilterModel?) {
        clearMarkers() // 기존 마커 제거
        selectedFilter?.let { filter ->
            Log.d("MapFragment", "선택된 필터: ${filter.query}")
        }
    }

    /**
     * 지도에 마커를 업데이트
     */
    private fun updateMapMarkers(restaurantList: List<RestaurantModel>) {
        if (!::naverMap.isInitialized) {
            Log.e("MapFragment", "NaverMap이 초기화되지 않았습니다.")
            return
        }

        val existingCoordinates = mutableListOf<LatLng>() // 기존 마커 좌표 저장

        restaurantList.forEachIndexed { index, restaurant ->
            val adjustedLatLng = getAdjustedLatLng(
                LatLng(restaurant.latitude.toDouble(), restaurant.longitude.toDouble()),
                existingCoordinates,
                index
            )

            existingCoordinates.add(adjustedLatLng)

            Marker().apply {
                position = adjustedLatLng
                map = naverMap
                captionText = restaurant.title
                setOnClickListener {
                    navigateToDetailFragment(restaurant) // 마커 클릭 시 상세 페이지로 이동
                    true
                }
                markers.add(this)
            }
        }
    }

    /**
     * 마커의 좌표를 조정하여 중복 방지
     */
    private fun getAdjustedLatLng(
        originalLatLng: LatLng,
        existingCoordinates: MutableList<LatLng>,
        index: Int
    ): LatLng {
        return if (isCloseToExistingCoordinates(existingCoordinates, originalLatLng)) {
            adjustMarkerPosition(originalLatLng.latitude, originalLatLng.longitude, index)
        } else {
            originalLatLng
        }
    }

    /**
     * 근사값 중복 여부를 확인
     */
    private fun isCloseToExistingCoordinates(existingCoordinates: List<LatLng>, target: LatLng): Boolean {
        val threshold = 0.0001 // 근사값 범위
        return existingCoordinates.any { coord ->
            abs(coord.latitude - target.latitude) <= threshold &&
                    abs(coord.longitude - target.longitude) <= threshold
        }
    }

    /**
     * 마커 위치를 약간 조정
     */
    private fun adjustMarkerPosition(latitude: Double, longitude: Double, index: Int): LatLng {
        val offset = 0.0001 * (index % 5) // 마커 간 오차 적용
        return LatLng(latitude + offset, longitude + offset)
    }

    /**
     * RecyclerView 초기화
     */
    private fun initRecyclerView() {
        filterAdapter = FilterAdapter { selectedFilter ->
            viewModel.selectFilter(selectedFilter) // 필터 선택 처리
        }
        binding.recyclerViewFilter.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }
    }

    /**
     * FAB 버튼 클릭 리스너 설정
     */
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

    /**
     * 필터 데이터 설정
     */
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

    /**
     * 마커 리스트 초기화
     */
    private fun clearMarkers() {
        markers.forEach { it.map = null }
        markers.clear()
    }

    /**
     * 상세 페이지로 이동
     */
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

    /**
     * 위치 추적을 한 번만 활성화
     */
    private fun enableLocationTrackingOnce() {
        val locationChangeListener = createLocationChangeListener()
        naverMap.addOnLocationChangeListener(locationChangeListener)
    }

    /**
     * 위치 변경 리스너 생성
     */
    private fun createLocationChangeListener(): OnLocationChangeListener {
        var isFirstLocationUpdate = true // 첫 번째 위치 업데이트 여부 확인

        return OnLocationChangeListener { location ->
            if (isFirstLocationUpdate) {
                moveCameraToLocation(location) // 첫 번째 위치 업데이트 시 카메라 이동
                isFirstLocationUpdate = false
                viewModel.toggleCameraInitialization()
            }
        }
    }

    /**
     * 카메라를 특정 위치로 이동
     */
    private fun moveCameraToLocation(location: Location) {
        val cameraUpdate = CameraUpdate.toCameraPosition(
            CameraPosition(
                LatLng(location.latitude, location.longitude),
                15.0
            )
        ).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

    /**
     * 현재 위치 오버레이 설정
     */
    private fun setOverlay() {
        locationOverlay = naverMap.locationOverlay.apply { isVisible = true }
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