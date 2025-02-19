package com.effort.feature.map

import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.collectFlow
import com.effort.feature.core.util.observeStateContinuouslyWithLifecycle
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentMapBinding
import com.effort.feature.model.map.RestaurantKey
import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.effort.presentation.model.map.FilterModel
import com.effort.presentation.viewmodel.home.restaurant.RestaurantViewModel
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
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.abs


@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: RestaurantViewModel by viewModels()
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var navController: NavController
    private lateinit var clusterer: Clusterer<RestaurantKey>
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var locationOverlay: LocationOverlay

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

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
                NaverMapOptions().camera(
                    CameraPosition(
                        LatLng(37.5112, 127.0590), // 초기 카메라 위치 (서울 강남)
                        14.0 // 줌 레벨
                    )
                ).indoorEnabled(true) // 실내 지도 활성화
                    .locationButtonEnabled(true) // 위치 버튼 활성화
            ).also {
                childFragmentManager.beginTransaction().replace(binding.map.id, it).commit()
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
     * 지도 초기화: 위치 소스 설정 및 오버레이 활성화
     */
    private fun initializeMap() {
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE) // 위치 소스 설정
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow // 위치 추적 모드 활성화
        setOverlay() // 현재 위치 오버레이 설정
    }

    /**
     * ViewModel 상태 관찰 및 UI 업데이트
     */
    private fun observeViewModel() {
        // 레스토랑 데이터 상태 관찰
        handleRestaurantState()

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
                showToast(getString(R.string.data_all_loaded))
            } else {
                showToast(message)
            }
        }
    }

    /**
     * 레스토랑 상태를 감지하여 지도 마커 업데이트
     */
    private fun handleRestaurantState() {
        observeStateContinuouslyWithLifecycle(
            stateFlow = viewModel.getRestaurantState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { restaurantData ->
            setupClusterer(restaurantData)
        }
    }

    /**
     * 필터 변경 감지
     */
    private fun handleFilterChange(selectedFilter: FilterModel?) {
        selectedFilter?.let { filter ->
            Timber.d("선택된 필터: ${filter.query}")
        }
    }

    /**
     * 지도에 마커 클러스터링 설정
     */
    private fun setupClusterer(restaurantList: List<RestaurantModel>) {
        clearClusterer()

        clusterer = Clusterer.Builder<RestaurantKey>().screenDistance(50.0) // 같은 건물 정도의 거리로 클러스터링
            .minZoom(8).maxZoom(14).animate(true)
            .clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
                override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                    super.updateClusterMarker(info, marker)
                    marker.icon = when {
                        info.size <= 7 -> MarkerIcons.CLUSTER_LOW_DENSITY
                        info.size in 8..15 -> MarkerIcons.CLUSTER_MEDIUM_DENSITY
                        else -> MarkerIcons.CLUSTER_HIGH_DENSITY
                    }
                    marker.captionText = "${info.size}개의 장소"
                }
            }).leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
                override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                    super.updateLeafMarker(info, marker)
                    val key = info.key as RestaurantKey

                    val existingCoordinates = mutableListOf<LatLng>()

                    marker.position = getAdjustedLatLng(
                        key.position, existingCoordinates, key.hashCode()
                    )

                    marker.captionText = key.title
                    marker.icon = MarkerIcons.GREEN

                    marker.setOnClickListener {
                        navigateToDetailFragment(restaurantList.first { it.title == key.title } // key.title로 레스토랑 찾기
                        )
                        true
                    }
                }
            }).build()

        val keyTagMap = mutableMapOf<RestaurantKey, Any?>()

        restaurantList.forEachIndexed { index, restaurant ->
            val adjustedLatLng = getAdjustedLatLng(
                LatLng(restaurant.latitude.toDouble(), restaurant.longitude.toDouble()),
                mutableListOf(),
                index
            )

            keyTagMap[RestaurantKey(restaurant.title, adjustedLatLng)] = null
        }

        clusterer.addAll(keyTagMap)
        clusterer.map = naverMap
    }

    /**
     * 마커의 좌표를 조정하여 중복 방지
     */
    private fun getAdjustedLatLng(
        originalLatLng: LatLng, existingCoordinates: MutableList<LatLng>, index: Int
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
    private fun isCloseToExistingCoordinates(
        existingCoordinates: List<LatLng>, target: LatLng
    ): Boolean {
        val threshold = 0.0001 // 근사값 범위
        return existingCoordinates.any { coord ->
            abs(coord.latitude - target.latitude) <= threshold && abs(coord.longitude - target.longitude) <= threshold
        }
    }

    /**
     * 마커 위치를 약간 조정 (랜덤성과 더 넓은 오차값 추가)
     */
    private fun adjustMarkerPosition(latitude: Double, longitude: Double, index: Int): LatLng {
        // 기본 오차값
        val baseOffset = 0.0002

        // 랜덤값 추가 (0.0 ~ 0.0001 범위)
        val randomOffset = (0..10).random() * 0.00001

        // 인덱스를 활용해 마커를 여러 방향으로 분산
        val latOffset = baseOffset * (index % 5) + randomOffset
        val lngOffset = baseOffset * ((index + 2) % 5) + randomOffset

        // 조정된 LatLng 반환
        return LatLng(latitude + latOffset, longitude + lngOffset)
    }

    /**
     * 필터 선택 UI 설정
     */
    private fun initRecyclerView() {
        filterAdapter = FilterAdapter { selectedFilter ->
            viewModel.selectFilter(selectedFilter)
        }
        binding.recyclerViewFilter.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }
    }

    /**
     * FAB 버튼 클릭 시 필터 적용 및 데이터 로드
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
                FilterModel(6, "분식", "분식"),
                FilterModel(7, "치킨", "치킨"),
                FilterModel(8, "국밥", "국밥"),
                FilterModel(9, "고기", "고기"),
                FilterModel(10, "회/해물", "회/해물"),
                FilterModel(11, "뷔페", "뷔페"),
                FilterModel(12, "패스트푸드", "패스트푸드"),
            )
        )
    }

    /**
     * 클러스터 초기화
     */
    private fun clearClusterer() {
        // 클러스터러 데이터 초기화
        if (::clusterer.isInitialized) {
            clusterer.clear() // 클러스터러 데이터 초기화
        }
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
                LatLng(location.latitude, location.longitude), 15.0
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
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
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