package com.effort.feature.home.restaurant.detail.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatest
import com.effort.feature.databinding.FragmentRestaurantInfoBinding
import com.effort.presentation.viewmodel.home.restaurant.RestaurantOverviewViewModel
import com.effort.presentation.viewmodel.home.restaurant.detail.info.RestaurantInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantInfoFragment :
    BaseFragment<FragmentRestaurantInfoBinding>(FragmentRestaurantInfoBinding::inflate) {

    private val viewModel: RestaurantInfoViewModel by viewModels()
    private val sharedViewModel: RestaurantOverviewViewModel by activityViewModels() // SharedViewModel 주입

    private lateinit var blogReviewListAdapter: BlogReviewListAdapter


    override fun initView() {
        setupRecyclerView()
        observeViewModel()
        setupInfiniteScrollListener(binding.recyclerviewBlogReview)
        observeSharedViewModelData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun setupRecyclerView() {
        blogReviewListAdapter = BlogReviewListAdapter()
        binding.recyclerviewBlogReview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = blogReviewListAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        observeStateLatest(
            stateFlow = viewModel.getBlogReviewState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { blogReviews ->
            blogReviewListAdapter.submitList(blogReviews) // ✅ 블로그 리뷰 리스트 업데이트
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
                    viewModel.fetchBlogReviews(
                        query = sharedViewModel.title.value,
                        region = sharedViewModel.region.value,
                        loadMore = true
                    )
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

    private fun observeSharedViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                sharedViewModel.title,
                sharedViewModel.region
            ) { title, region ->
                title to region
            }.collectLatest { (title, region) ->
                if (title.isNotEmpty() && region.isNotEmpty()) {
                    Log.d("RestaurantInfoFragment", "Title: $title, Region: $region")
                    viewModel.fetchBlogReviews(title, region)
                }
            }
        }
    }
}