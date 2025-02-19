package com.effort.feature.home.main

import android.content.Context
import android.os.Bundle
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
import com.effort.feature.core.util.observeStateLatestWithLifecycleOnFragment
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentHomeBinding
import com.effort.presentation.model.home.CategoryModel
import com.effort.presentation.UiState
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * ViewModel의 상태를 관찰하여 UI 업데이트
     */
    private fun observeViewModel() {
        handleUserUpdateState()

        // 카테고리 목록이 변경될 때 RecyclerView 업데이트
        collectFlow(homeViewModel.categories) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    /**
     * 사용자 정보가 변경될 때 UI를 업데이트
     */
    private fun handleUserUpdateState() {
        observeStateLatestWithLifecycleOnFragment(
            stateFlow = myPageViewModel.userUpdateState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { userData ->
            binding.userName.text = getDisplayName(userData.nickname, userData.name)
        }
    }

    /**
     * 닉네임이 비어있으면 실명을 반환
     */
    private fun getDisplayName(nickname: String, name: String): String {
        return nickname.ifBlank { name }
    }

    /**
     * RecyclerView를 초기화하고 어댑터를 설정
     */
    private fun setupRecyclerView() {
        categoryAdapter = CategoryListAdapter { category ->
            navigateToRestaurantFragment(category)
        }
        binding.recyclerviewCategory.apply {
            adapter = categoryAdapter
            setHasFixedSize(true)
        }

        suggestionAdapter = SuggestionListAdapter { selectedSuggestion ->
            binding.searchKeyword.setText(selectedSuggestion)
            binding.recyclerviewSuggestion.visibility = View.GONE
        }
        binding.recyclerviewSuggestion.adapter = suggestionAdapter
    }

    /**
     * 검색 완료 버튼 클릭 리스너 설정
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
     * 검색 아이콘 클릭 리스너 설정
     */
    private fun setupEndIconClickListener() {
        binding.searchView.setEndIconOnClickListener {
            handleSearchAction(binding.searchKeyword.text.toString().trim())
        }
    }

    /**
     * 검색 동작 처리
     */
    private fun handleSearchAction(query: String) {
        if (query.isNotEmpty()) {
            hideKeyboard()
            navigateToRestaurantFragment(query)
        } else {
            showToast("검색어를 입력해주세요")
        }
    }

    /**
     * 키보드 숨기기
     */
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**
     * 검색어 입력 리스너 설정
     */
    private fun setupSearchKeywordListener() {
        binding.searchKeyword.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            if (query.isNotEmpty()) {
                homeViewModel.fetchSuggestions(query) // 검색어 추천 요청
            } else {
                binding.recyclerviewSuggestion.visibility = View.GONE
            }
        }
    }

    /**
     * 검색어 추천 상태를 관찰하여 UI 업데이트
     */
    private fun observeSuggestionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getSuggestionState.collect { state ->
                when (state) {
                    is UiState.Loading -> binding.recyclerviewSuggestion.visibility = View.GONE
                    is UiState.Success -> {
                        val keywordList = state.data
                        suggestionAdapter.submitList(keywordList)
                        binding.recyclerviewSuggestion.visibility = View.VISIBLE
                    }

                    is UiState.Error, is UiState.Empty -> binding.recyclerviewSuggestion.visibility =
                        View.GONE
                }
            }
        }
    }

    private fun setupCategoryList() {
        homeViewModel.setCategories(
            listOf(
                CategoryModel(getString(R.string.korean_food), R.drawable.korean_food_image),
                CategoryModel(getString(R.string.chinese_food), R.drawable.chinese_food_image),
                CategoryModel(getString(R.string.western_food), R.drawable.western_food_image),
                CategoryModel(getString(R.string.japanese_food), R.drawable.japanese_food_image),
                CategoryModel(getString(R.string.asian_food), R.drawable.asian_food_image),
                CategoryModel(getString(R.string.bunsik_food), R.drawable.bunsik_food_image),
                CategoryModel(getString(R.string.chicken), R.drawable.chicken_image),
                CategoryModel(getString(R.string.gukbap), R.drawable.gukbap_image),
                CategoryModel(getString(R.string.grilled_meat), R.drawable.grilled_meat),
                CategoryModel(getString(R.string.seafood), R.drawable.seafood_image),
                CategoryModel(getString(R.string.buffet), R.drawable.buffet_image),
                CategoryModel(getString(R.string.fastfood), R.drawable.fastfood_image),
            )
        )
    }

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
}