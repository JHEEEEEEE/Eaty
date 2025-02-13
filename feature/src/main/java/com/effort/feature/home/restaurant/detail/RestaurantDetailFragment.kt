package com.effort.feature.home.restaurant.detail

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.effort.feature.R
import com.effort.feature.community.RestaurantDetailAdapter
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.extractGuDongFromSeoulAddress
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentRestaurantDetailBinding
import com.effort.presentation.UiState
import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.effort.presentation.model.home.restaurant.navigation.NavigationPathModel
import com.effort.presentation.viewmodel.home.restaurant.RestaurantOverviewViewModel
import com.effort.presentation.viewmodel.home.restaurant.favorites.RestaurantFavoritesViewModel
import com.effort.presentation.viewmodel.home.restaurant.navigation.NavigationViewModel
import com.effort.presentation.viewmodel.share.ShareViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantDetailFragment :
    BaseFragment<FragmentRestaurantDetailBinding>(FragmentRestaurantDetailBinding::inflate),
    OnMapReadyCallback {
    private lateinit var navController: NavController
    private val args: RestaurantDetailFragmentArgs by navArgs() // SafeArgs로 데이터 받기
    private val restaurantFavoritesViewModel: RestaurantFavoritesViewModel by viewModels()
    private val shareViewModel: ShareViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by viewModels()
    private val restaurantOverviewViewModel: RestaurantOverviewViewModel by activityViewModels()

    // Map 관련 변수
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var restaurantMarker: Marker
    private lateinit var userMarker: Marker
    private lateinit var pathOverlay: PathOverlay
    private var isFirstLocationUpdate = true // Fragment가 새로 생성될 때 자동 초기화


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private fun setupMapFragment() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.naver_map_container) as? MapFragment
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.naver_map_container, it)
                        .commit()
                }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        pathOverlay = PathOverlay()

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        binding.mapProgressBar.visibility = View.VISIBLE

        val latitude = args.latitude.toDoubleOrNull()
        val longitude = args.longitude.toDoubleOrNull()

        if (latitude == null || longitude == null || latitude.isNaN() || longitude.isNaN()) {
            return // 좌표가 잘못되었으면 실행 중단
        }

        val restaurantLatLng = LatLng(latitude, longitude)

        setupMarkers(restaurantLatLng)

        naverMap.addOnLocationChangeListener { location ->
            if (isFirstLocationUpdate) { // 최초 1회만 실행
                val userLatLng = LatLng(location.latitude, location.longitude)
                updateUserLocation(userLatLng)

                // 🚀 ViewModel에 사용자 위치 & 식당 위치 전달
                navigationViewModel.fetchNavigationPath(
                    NavigationPathModel(userLatLng.latitude, userLatLng.longitude),
                    NavigationPathModel(restaurantLatLng.latitude, restaurantLatLng.longitude)
                )

                isFirstLocationUpdate = false // 이후에는 실행되지 않도록 설정
            }
        }
    }

    private fun observeNavigationPathState() {
        viewLifecycleOwner.lifecycleScope.launch {
            navigationViewModel.getNavigationPathState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.mapProgressBar.visibility = View.VISIBLE
                    }

                    is UiState.Success -> {
                        drawPath(state.data)
                    }

                    is UiState.Error -> {
                        binding.mapProgressBar.visibility = View.GONE
                        Log.e(
                            "RestaurantDetailFragment",
                            "Navigation API Error: ${state.exception.message}"
                        )
                    }

                    is UiState.Empty -> {
                        binding.mapProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupMarkers(restaurantLatLng: LatLng) {
        if (restaurantLatLng.latitude.isNaN() || restaurantLatLng.longitude.isNaN()) {
            return
        }

        restaurantMarker = Marker().apply {
            position = restaurantLatLng
            icon = Marker.DEFAULT_ICON
            map = naverMap
        }

        userMarker = Marker().apply {
            position = LatLng(0.0, 0.0) // 기본 위치 (임시 값)
            icon = Marker.DEFAULT_ICON
            map = naverMap
        }
    }


    private fun updateUserLocation(userLatLng: LatLng) {

        if (userLatLng.latitude.isNaN() || userLatLng.longitude.isNaN() ||
            restaurantMarker.position.latitude.isNaN() || restaurantMarker.position.longitude.isNaN()
        ) {
            return
        }

        userMarker.position = userLatLng

        try {
            val bounds = LatLngBounds.from(userLatLng, restaurantMarker.position)
            val cameraUpdate = CameraUpdate.fitBounds(bounds, 240).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)

        } catch (e: Exception) {
            Log.e("ERROR", "Failed to update camera bounds: ${e.message}")
        }
    }

    private fun drawPath(pathList: List<NavigationPathModel>) {
        if (pathList.isEmpty()) {
            return
        }

        pathOverlay.map = null

        pathOverlay = PathOverlay().apply {
            coords = pathList.map { LatLng(it.latitude, it.longitude) }
            color = Color.BLUE
            width = 10
            outlineColor = Color.DKGRAY
            outlineWidth = 3
            map = naverMap
        }

        binding.mapProgressBar.visibility = View.GONE
    }

    override fun initView() {
        setupTabLayoutAndViewPager()
        setupClickListeners()
        observeUserState() // 사용자 상태 관찰
        observeFavoriteCheckState() // 찜 여부 확인 상태 관찰
        observeAddFavoriteState()
        observeRemoveFavoriteState()
        observeShareLinkState()
        setupRestaurantOverviewViewModelData()
        setupMapFragment()
        observeNavigationPathState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments // ✅ 딥링크에서 전달된 데이터 확인

        if (bundle != null && bundle.containsKey("title")) {
            Log.d("RestaurantDetailFragment", "arguments 사용 (딥링크)")
            bindRestaurantDetails(bundle)
        } else {
            Log.d("RestaurantDetailFragment", "SafeArgs 사용")
            bindRestaurantDetails(args) // ✅ Fragment 간 이동은 SafeArgs 사용
        }

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

        val restaurantDetailAdapter = RestaurantDetailAdapter(this)
        viewPager.adapter = restaurantDetailAdapter

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
            restaurantRoadNameAddress.text = args.roadNameAddress
            restaurantDistance.text = getString(R.string.distance, args.distance)
            restaurantNumber.text = args.phoneNumber
            restaurantWebpage.text = args.placeUrl
        }
    }

    private fun bindRestaurantDetails(bundle: Bundle) {
        with(binding) {
            restaurantTitle.text = bundle.getString("title", "제목 없음")
            restaurantLotNumberAddress.text = bundle.getString("lotNumberAddress", "주소 없음")
            restaurantRoadNameAddress.text = bundle.getString("roadNameAddress", "도로명 주소 없음")
            restaurantDistance.text = getString(R.string.distance, bundle.getString("distance", "0m"))
            restaurantNumber.text = bundle.getString("phoneNumber", "전화번호 없음")
            restaurantWebpage.text = bundle.getString("placeUrl", "웹페이지 없음")
        }
    }

    private fun setupRestaurantOverviewViewModelData() {
        with(restaurantOverviewViewModel) {
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

    private fun setupClickListeners() {
        listOf(
            binding.restaurantWebpage to { openUrl(args.placeUrl) },
            binding.restaurantNumber to { dialPhoneNumber(args.phoneNumber) },
            binding.shareButton to { shareRestaurant() },  // ✅ 공유 기능 함수 분리
            binding.favoriteButton to { handleFavoriteClick() }
        ).forEach { (view, action) ->
            view.setOnClickListener { action() }
        }
    }

    // 웹사이트 열기
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    // 전화 걸기
    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    // 찜하기 버튼 클릭 처리
    private fun handleFavoriteClick() {
        val restaurant = createRestaurantModel()
        if (!binding.favoriteButton.isSelected) {
            restaurantFavoritesViewModel.addRestaurantToFavorites(restaurant)
        } else {
            restaurantFavoritesViewModel.removeRestaurantFromFavorites(restaurant.title)
        }
    }

    // 공유 기능 함수 (shareViewModel 호출)
    private fun shareRestaurant() {
        shareViewModel.shareContent(args.title, args.lotNumberAddress, args.roadNameAddress, args.distance, args.phoneNumber, args.placeUrl)
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

    private fun observeShareLinkState() {
        viewLifecycleOwner.lifecycleScope.launch {
            shareViewModel.shareLinkState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        Log.d("restaurantDetailFragment", "${state.data}")
                        openKakaoShare(state.data)
                        showToast("공유를 시작합니다.")
                        Log.d("RestaurantDetailFragment", "Successfully Get shareLink")
                        shareViewModel.resetShareState()
                    }

                    is UiState.Loading -> {
                        Log.d("RestaurantDetailFragment", "Trying to get shareLink...")
                    }

                    is UiState.Error -> {
                        showToast("오류 발생: ${state.exception.message}")
                        Log.e(
                            "RestaurantDetailFragment",
                            "Error get shareLink: ${state.exception.message}"
                        )
                    }

                    is UiState.Empty -> Log.d(
                        "RestaurantDetailFragment",
                        "Get shareLink is empty"
                    )
                }
            }
        }
    }

    private fun isKakaoTalkInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo("com.kakao.talk", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openKakaoShare(shareUrl: String) {
        if (!isKakaoTalkInstalled(requireContext())) {
            // 사용자에게 설치 필요 안내
            Toast.makeText(requireContext(), "카카오톡이 설치되어 있지 않습니다. 설치 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()

            // 카카오톡 설치 페이지로 이동 (Play Store)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakaotalk_playstorelink)))
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                // Play Store 앱이 없을 경우 웹으로 이동
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakaotalk_weblink)))
                startActivity(webIntent)
            }
            return
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(shareUrl))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "공유할 수 있는 앱이 없습니다.", Toast.LENGTH_SHORT).show()
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
