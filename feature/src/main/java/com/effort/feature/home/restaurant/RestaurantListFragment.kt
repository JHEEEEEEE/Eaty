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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentRestaurantBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.home.restaurant.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantListFragment :
    BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {
    private val viewModel: RestaurantViewModel by viewModels()
    private val args: RestaurantListFragmentArgs by navArgs() // SafeArgs로 데이터 받기
    private lateinit var restaurantListAdapter: RestaurantListAdapter
    private var layoutManagerState: Parcelable? = null

    // 권한 요청 코드
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun initView() {
        initRecyclerView()
        observeGetRestaurantState()

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
        // 스크롤 상태 저장
        layoutManagerState = binding.recyclerviewRestaurant.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        layoutManagerState?.let {
            binding.recyclerviewRestaurant.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun initRecyclerView() {
        // 일반 레스토랑 리스트 어댑터
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
            findNavController().navigate(action) // 일반 리스트에서 상세로 이동
        }
        binding.recyclerviewRestaurant.apply {
            adapter = restaurantListAdapter
        }
    }

    private fun observeGetRestaurantState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRestaurantState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressIndicator.showLoading(true)
                            binding.recyclerviewRestaurant.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewRestaurant.visibility = View.VISIBLE
                            restaurantListAdapter.submitList(state.data)
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
    }

    // 위치 권한 확인
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 권한 요청
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용 시 위치 기반으로 데이터 조회
                viewModel.fetchRestaurants(args.query)
            } else {
                // 권한 거부 시 일반 데이터 조회
                viewModel.fetchRestaurants(args.query)
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupInfiniteScrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 스크롤 방향이 아래로 이동할 때만 동작
                if (dy <= 0) return

                // 무한 스크롤 조건 검사 및 페이지 로딩
                if (shouldLoadMoreData(recyclerView)) {
                    viewModel.fetchRestaurants(query = args.query, loadMore = true)
                }
            }
        })
    }

    // 무한 스크롤 로딩 조건 검사 함수
    private fun shouldLoadMoreData(recyclerView: RecyclerView): Boolean {
        // LayoutManager 타입 체크
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            ?: return false

        // 스크롤 항목 개수 및 위치 계산
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        // 무한 스크롤 조건 반환
        return !viewModel.isLoading && !viewModel.isLastPage &&
                (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                firstVisibleItemPosition >= 0
    }

    private fun loadData() {
        // 권한 확인 및 요청 처리
        if (checkLocationPermission()) {
            // 권한 허용 시 데이터 로드
            loadRestaurantData()
        } else {
            // 권한 요청
            requestLocationPermission()
        }
    }

    private fun loadRestaurantData() {
        // 데이터 복원: 캐시된 데이터가 있으면 복원, 없으면 새로 로드
        if (viewModel.cachedData.value.isEmpty()) {
            viewModel.fetchRestaurants(args.query) // 새로 로드
        } else {
            viewModel.loadCachedData() // 캐시된 데이터 로드
        }
    }
}
