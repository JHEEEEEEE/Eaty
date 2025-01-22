package com.effort.feature.home.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.collectFlow
import com.effort.feature.core.util.showLoading
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentHomeBinding
import com.effort.presentation.model.category.CategoryModel
import com.effort.presentation.UiState
import com.effort.presentation.model.auth.FirebaseUserModel
import com.effort.presentation.viewmodel.home.category.CategoryViewModel
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryListAdapter

    override fun initView() {
        setupRecyclerView()
        setupMapButtonClickListener()
        observeViewModel()
        setupCategoryList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    /**
     * ViewModel 상태를 관찰
     */
    private fun observeViewModel() {
        // 유저 업데이트 상태 관찰
        collectFlow(myPageViewModel.userUpdateState) { state ->
            handleUserUpdateState(state) // 유저 상태 처리 로직 분리
        }

        // 카테고리 리스트 상태 관찰
        collectFlow(categoryViewModel.categories) { categories ->
            handleCategoryUpdate(categories) // 카테고리 상태 처리 로직 분리
        }
    }

    /**
     * 유저 업데이트 상태 처리
     */
    private fun handleUserUpdateState(state: UiState<FirebaseUserModel>) {
        val progressIndicator = binding.progressCircular.progressBar

        when (state) {
            is UiState.Loading -> {
                progressIndicator.showLoading(true)
            }

            is UiState.Success -> {
                progressIndicator.showLoading(false)
                binding.userName.text =
                    getDisplayName(state.data.nickname, state.data.name)
            }

            is UiState.Error -> {
                progressIndicator.showLoading(false)
                showToast("유저 업데이트에 실패했습니다.") // 에러 메시지 표시
            }

            is UiState.Empty -> {
                progressIndicator.showLoading(false)
            }
        }
    }

    /**
     * 카테고리 업데이트 상태 처리
     */
    private fun handleCategoryUpdate(categories: List<CategoryModel>) {
        categoryAdapter.submitList(categories)
    }

    private fun getDisplayName(nickname: String, name: String): String {
        return nickname.ifBlank { name }
    }

    // 1. RecyclerView 초기화
    private fun setupRecyclerView() {
        categoryAdapter = CategoryListAdapter { category ->
            navigateToRestaurantFragment(category) // 클릭 시 이동
        }

        binding.recyclerviewCategory.apply {
            adapter = categoryAdapter
        }
    }

    // 2. 카테고리 데이터 설정
    private fun setupCategoryList() {
        categoryViewModel.setCategories(
            listOf(
                CategoryModel("한식", R.drawable.korean_food_image),
                CategoryModel("중식", R.drawable.chinese_food_image),
                CategoryModel("양식", R.drawable.western_food_image),
                CategoryModel("일식", R.drawable.japanese_food_image),
                CategoryModel("아시아음식", R.drawable.asian_food_image),
                CategoryModel("분식", R.drawable.cafe_image),
                CategoryModel("치킨", R.drawable.cafe_image),
                CategoryModel("국밥", R.drawable.cafe_image),
                CategoryModel("고기", R.drawable.cafe_image),
                CategoryModel("회/해물", R.drawable.cafe_image),
                CategoryModel("뷔페", R.drawable.cafe_image),
                CategoryModel("패스트푸드", R.drawable.cafe_image),
            )
        )
    }

    // 3. 클릭 시 RestaurantFragment로 이동
    private fun navigateToRestaurantFragment(category: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToRestaurantFragment(category)
        findNavController().navigate(action)
    }

    private fun navigateToMapFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToMapFragment()
        findNavController().navigate(action)
    }

    private fun setupMapButtonClickListener() {
        binding.btnMap.setOnClickListener {
            navigateToMapFragment()
        }
    }
}
