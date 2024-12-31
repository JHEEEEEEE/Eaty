package com.effort.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentRestaurantBinding
import com.effort.presentation.UiState
import com.effort.presentation.model.home.SortTypeModel
import com.effort.presentation.viewmodel.home.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantFragment : BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {
    private val viewModel: RestaurantViewModel by viewModels()
    private val args: RestaurantFragmentArgs by navArgs() // SafeArgs로 데이터 받기
    private lateinit var restaurantListAdapter: RestaurantListAdapter

    // 권한 요청 코드
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun initView() {
        initRecyclerView()
        observeGetRestaurantState()

        // 권한 확인 및 요청 처리
        if (checkLocationPermission()) {
            viewModel.fetchRestaurants(args.query) // 권한 허용 시 데이터 로드
        } else {
            requestLocationPermission() // 권한 요청
        }
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
        restaurantListAdapter = RestaurantListAdapter()
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
}
