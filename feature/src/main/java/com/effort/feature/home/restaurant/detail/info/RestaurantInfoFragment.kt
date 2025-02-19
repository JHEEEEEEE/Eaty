package com.effort.feature.home.restaurant.detail.info

import android.os.Bundle
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
    private val sharedViewModel: RestaurantOverviewViewModel by activityViewModels()
    private lateinit var blogReviewListAdapter: BlogReviewListAdapter

    override fun initView() {
        setupRecyclerView()
        observeViewModel()
        setupInfiniteScrollListener(binding.recyclerviewBlogReview)
        observeSharedViewModelData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * 블로그 리뷰 목록을 표시할 RecyclerView 설정
     */
    private fun setupRecyclerView() {
        blogReviewListAdapter = BlogReviewListAdapter()
        binding.recyclerviewBlogReview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = blogReviewListAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * ViewModel에서 블로그 리뷰 데이터를 감지하여 UI 업데이트
     */
    private fun observeViewModel() {
        observeStateLatest(
            stateFlow = viewModel.getBlogReviewState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { blogReviews ->
            blogReviewListAdapter.submitList(blogReviews)
        }
    }

    /**
     * 사용자가 스크롤을 끝까지 내리면 추가 데이터를 요청
     * - 스크롤이 아래로 이동할 때만 동작
     * - 추가 데이터 로딩 조건이 충족되면 새로운 페이지 요청
     */
    private fun setupInfiniteScrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy <= 0) return // 위로 스크롤할 때는 무시

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

        return !viewModel.isLoading && !viewModel.isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0
    }

    /**
     * SharedViewModel에서 제목과 지역 데이터를 가져와 블로그 리뷰 요청
     * - 제목 또는 지역이 변경될 경우 새로운 데이터를 요청
     */
    private fun observeSharedViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                sharedViewModel.title, sharedViewModel.region
            ) { title, region ->
                title to region
            }.collectLatest { (title, region) ->
                if (title.isNotEmpty() && region.isNotEmpty()) {
                    viewModel.fetchBlogReviews(title, region)
                }
            }
        }
    }
}