package com.effort.feature.home.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.collectFlow
import com.effort.feature.core.util.showLoading
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentHomeBinding
import com.effort.presentation.model.home.CategoryModel
import com.effort.presentation.UiState
import com.effort.presentation.model.auth.FirebaseUserModel
import com.effort.presentation.viewmodel.home.HomeViewModel
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var suggestionAdapter: SuggestionListAdapter

    override fun initView() {
        setupRecyclerView()
        setupMapButtonClickListener()
        observeViewModel()
        setupCategoryList()
        setupSearchKeywordListener()
        observeSuggestionState()
        setupDoneButtonListener()
        setupEndIconClickListener()
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
        collectFlow(homeViewModel.categories) { categories ->
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
                showToast(getString(R.string.user_update_error)) // 에러 메시지 표시
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

        suggestionAdapter = SuggestionListAdapter { selectedSuggestion ->
            binding.searchKeyword.setText(selectedSuggestion)
            binding.recyclerviewSuggestion.visibility = View.GONE
        }

        binding.recyclerviewSuggestion.apply {
            adapter = suggestionAdapter
        }
    }

    // 2. 카테고리 데이터 설정
    private fun setupCategoryList() {
        homeViewModel.setCategories(
            listOf(
                CategoryModel(getString(R.string.korean_food), R.drawable.korean_food_image),
                CategoryModel(getString(R.string.chinese_food), R.drawable.chinese_food_image),
                CategoryModel(getString(R.string.western_food), R.drawable.western_food_image),
                CategoryModel(getString(R.string.japanese_food), R.drawable.japanese_food_image),
                CategoryModel(getString(R.string.asian_food), R.drawable.asian_food_image),
                CategoryModel(getString(R.string.bunsik_food), R.drawable.cafe_image),
                CategoryModel(getString(R.string.chicken), R.drawable.cafe_image),
                CategoryModel(getString(R.string.gukbap), R.drawable.cafe_image),
                CategoryModel(getString(R.string.grilled_meat), R.drawable.cafe_image),
                CategoryModel(getString(R.string.seafood), R.drawable.cafe_image),
                CategoryModel(getString(R.string.buffet), R.drawable.cafe_image),
                CategoryModel(getString(R.string.fastfood), R.drawable.cafe_image),
            )
        )
    }

    // 3. 클릭 시 RestaurantFragment로 이동
    private fun navigateToRestaurantFragment(query: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToRestaurantFragment(query)
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

    /**
     * 키보드 완료 버튼 처리
     */
    private fun setupDoneButtonListener() {
        binding.searchKeyword.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleSearchAction(textView.text.toString().trim())
                true
            } else {
                false
            }
        }
    }

    /**
     * 검색 아이콘 클릭 처리
     */
    private fun setupEndIconClickListener() {
        binding.searchView.setEndIconOnClickListener {
            handleSearchAction(binding.searchKeyword.text.toString().trim())
        }
    }

    /**
     * 검색 동작 공통 처리
     */
    private fun handleSearchAction(query: String) {
        if (query.isNotEmpty()) {
            hideKeyboard() // 키보드 닫기
            navigateToRestaurantFragment(query)
        } else {
            showToast("검색어를 입력해주세요")
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun setupSearchKeywordListener() {
        binding.searchKeyword.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            if (query.isNotEmpty()) {
                homeViewModel.fetchSuggestions(query) // ViewModel에 검색어 전달
            } else {
                binding.recyclerviewSuggestion.visibility = View.GONE // 추천어 숨김
            }
        }
    }


    private fun observeSuggestionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getSuggestionState.collect { state ->
                when (state) {
                    is UiState.Loading -> binding.recyclerviewSuggestion.visibility = View.GONE
                    is UiState.Success -> {
                        val keywordList = state.data
                        Log.d("HomeFragment", "$keywordList")
                        Log.d("HomeFragment", "${keywordList.size}")
                        suggestionAdapter.submitList(keywordList)
                        binding.recyclerviewSuggestion.visibility = View.VISIBLE
                    }
                    is UiState.Error -> binding.recyclerviewSuggestion.visibility = View.GONE
                    is UiState.Empty -> binding.recyclerviewSuggestion.visibility = View.GONE
                }
            }
        }
    }
}
