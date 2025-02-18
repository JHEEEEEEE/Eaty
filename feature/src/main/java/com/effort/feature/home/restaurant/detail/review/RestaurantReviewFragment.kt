package com.effort.feature.home.restaurant.detail.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatest
import com.effort.feature.databinding.FragmentRestaurantReviewBinding
import com.effort.presentation.viewmodel.home.restaurant.RestaurantOverviewViewModel
import com.effort.presentation.viewmodel.home.restaurant.detail.review.RestaurantReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantReviewFragment :
    BaseFragment<FragmentRestaurantReviewBinding>(FragmentRestaurantReviewBinding::inflate) {

    private val viewModel: RestaurantReviewViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter
    private val sharedViewModel: RestaurantOverviewViewModel by activityViewModels()

    override fun initView() {
        setupRecyclerView()
        observeViewModel()

        // SharedViewModel에서 식당 이름을 감지하여 댓글 목록을 불러옴
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.title.collectLatest { restaurantId ->
                setupSendButton(restaurantId)
                viewModel.getComments(restaurantId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantReviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * 댓글 목록을 표시하는 RecyclerView 설정
     */
    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter()
        binding.recyclerviewComment.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * ViewModel에서 댓글 상태를 감지하여 UI 업데이트
     */
    private fun observeViewModel() {
        observeStateLatest(
            stateFlow = viewModel.getCommentState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { commentData ->
            commentAdapter.submitList(commentData)
        }

        observeStateLatest(
            stateFlow = viewModel.addCommentState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { _ ->
            Toast.makeText(requireContext(), "댓글 추가 성공!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 댓글 전송 버튼 클릭 이벤트 설정
     * - 사용자가 입력한 댓글이 비어있지 않으면 ViewModel에 전달
     * - 전송 후 입력창 초기화
     *
     * @param restaurantId 댓글이 추가될 식당의 ID
     */
    private fun setupSendButton(restaurantId: String) {
        binding.sendMsg.setOnClickListener {
            val content = binding.edittextMsg.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.addComment(restaurantId, content)
                binding.edittextMsg.text?.clear()
            } else {
                Toast.makeText(requireContext(), "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}