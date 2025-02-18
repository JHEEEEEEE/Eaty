package com.effort.feature.home.restaurant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateContinuouslyWithLifecycle
import com.effort.feature.databinding.FragmentRestaurantBinding
import com.effort.presentation.viewmodel.home.restaurant.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantListFragment :
    BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {

    private val viewModel: RestaurantViewModel by viewModels()
    private val args: RestaurantListFragmentArgs by navArgs()
    private lateinit var restaurantListAdapter: RestaurantListAdapter
    private var layoutManagerState: Parcelable? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun initView() {
        initRecyclerView()
        observeViewModel()
        loadData()
        setupInfiniteScrollListener(binding.recyclerviewRestaurant)
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

    override fun onPause() {
        super.onPause()
        layoutManagerState = binding.recyclerviewRestaurant.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        layoutManagerState?.let {
            binding.recyclerviewRestaurant.layoutManager?.onRestoreInstanceState(it)
        }
    }

    /**
     * 레스토랑 목록을 표시하는 RecyclerView 설정
     */
    private fun initRecyclerView() {
        restaurantListAdapter = RestaurantListAdapter { restaurant ->
            val action = RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantDetailFragment(
                title = restaurant.title,
                lotNumberAddress = restaurant.lotNumberAddress,
                roadNameAddress = restaurant.roadNameAddress,
                distance = restaurant.distance,
                phoneNumber = restaurant.phoneNumber,
                placeUrl = restaurant.placeUrl,
                latitude = restaurant.latitude,
                longitude = restaurant.longitude
            )
            findNavController().navigate(action)
        }
        binding.recyclerviewRestaurant.adapter = restaurantListAdapter
    }

    /**
     * ViewModel에서 레스토랑 데이터를 감지하여 UI 업데이트
     */
    private fun observeViewModel() {
        observeStateContinuouslyWithLifecycle(
            stateFlow = viewModel.getRestaurantState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { restaurantData ->
            restaurantListAdapter.submitList(restaurantData)
        }
    }

    /**
     * 위치 권한 확인
     *
     * @return 위치 권한이 허용된 경우 true
     */
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 위치 권한 요청
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * 권한 요청 결과 처리
     * - 권한이 허용되면 위치 기반으로 레스토랑 데이터 조회
     * - 권한이 거부되면 일반 데이터 조회
     *
     * @param requestCode 요청 코드
     * @param permissions 요청된 권한 배열
     * @param grantResults 권한 허용 결과 배열
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.fetchRestaurants(args.query)
            } else {
                viewModel.fetchRestaurants(args.query)
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 스크롤이 끝까지 내려가면 추가 데이터를 요청
     * - 스크롤이 아래로 이동할 때만 동작
     * - 추가 데이터 로딩 조건이 충족되면 새로운 페이지 요청
     */
    private fun setupInfiniteScrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy <= 0) return

                if (shouldLoadMoreData(recyclerView)) {
                    viewModel.fetchRestaurants(query = args.query, loadMore = true)
                }
            }
        })
    }

    /**
     * 무한 스크롤이 필요한 조건 검사
     * - 현재 로딩 중이면 추가 요청하지 않음
     * - 마지막 페이지에 도달했다면 추가 요청하지 않음
     * - 현재 보이는 아이템 개수 + 첫 번째 보이는 아이템 위치가 전체 개수 이상이면 요청
     *
     * @return 추가 데이터 요청이 필요한 경우 true
     */
    private fun shouldLoadMoreData(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return false

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        return !viewModel.isLoading && !viewModel.isLastPage &&
                (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                firstVisibleItemPosition >= 0
    }

    /**
     * 레스토랑 데이터를 로드
     * - 권한이 허용되면 위치 기반 데이터 조회
     * - 권한이 없으면 권한 요청 후 데이터 조회
     */
    private fun loadData() {
        if (checkLocationPermission()) {
            loadRestaurantData()
        } else {
            requestLocationPermission()
        }
    }

    /**
     * 레스토랑 데이터를 불러오거나 캐시된 데이터를 복원
     * - 캐시된 데이터가 없으면 새로 로드
     * - 캐시된 데이터가 있으면 복원하여 사용
     */
    private fun loadRestaurantData() {
        if (viewModel.cachedData.value.isEmpty()) {
            viewModel.fetchRestaurants(args.query)
        } else {
            viewModel.loadCachedData()
        }
    }
}