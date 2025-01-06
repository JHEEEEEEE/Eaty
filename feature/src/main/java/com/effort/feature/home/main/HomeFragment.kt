package com.effort.feature.home.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentHomeBinding
import com.effort.feature.model.category.CategoryModel
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryListAdapter

    override fun initView() {
        observeUserUpdates()
        setupRecyclerView()
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

    private fun observeUserUpdates() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userUpdateState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressIndicator.showLoading(true)
                        }
                        is UiState.Success -> {
                            progressIndicator.showLoading(false)
                            binding.userName.text = getDisplayName(state.data.nickname, state.data.name)
                        }
                        is UiState.Error -> {
                            progressIndicator.showLoading(false)
                        }
                        is UiState.Empty -> {
                            progressIndicator.showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun getDisplayName(nickname: String, name: String): String {
        return nickname.ifBlank { name }
    }

    // 1. RecyclerView 초기화
    private fun setupRecyclerView() {
        categoryAdapter = CategoryListAdapter { category ->
            navigateToRestaurantFragment(category) // 클릭 시 이동
        }

        binding.recyclerView.apply {
            adapter = categoryAdapter
        }
    }

    // 2. 카테고리 데이터 설정
    private fun setupCategoryList() {
        val categoryList = listOf(
            CategoryModel("한식", R.drawable.korean_food_image),
            CategoryModel("중식", R.drawable.chinese_food_image),
            CategoryModel("양식", R.drawable.western_food_image),
            CategoryModel("일식", R.drawable.japanese_food_image),
            CategoryModel("아시아음식", R.drawable.asian_food_image),
            CategoryModel("카페", R.drawable.cafe_image)
        )
        categoryAdapter.submitList(categoryList)
    }

    // 3. 클릭 시 RestaurantFragment로 이동
    private fun navigateToRestaurantFragment(category: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToRestaurantFragment(category)
        findNavController().navigate(action)
    }
}
