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
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentRestaurantReviewBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.home.SharedRestaurantViewModel
import com.effort.presentation.viewmodel.home.detail.review.RestaurantReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantReviewFragment :
    BaseFragment<FragmentRestaurantReviewBinding>(FragmentRestaurantReviewBinding::inflate) {

    private val viewModel: RestaurantReviewViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter

    private val sharedViewModel: SharedRestaurantViewModel by activityViewModels()

    override fun initView() {
        setupRecyclerView()
        observeViewModel()

        // SharedViewModel에서 식당이름 데이터를 수집하여 사용
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

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter()
        binding.recyclerviewComment.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            setHasFixedSize(true) // 성능 최적화
        }
    }

    private fun observeViewModel() {
        observeGetCommentState()
        observeAddCommentState()
    }

    private fun observeGetCommentState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCommentState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressIndicator.showLoading(true)
                        binding.recyclerviewComment.visibility = View.GONE
                    }

                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        binding.recyclerviewComment.visibility = View.VISIBLE
                        commentAdapter.submitList(state.data) // 댓글 목록 업데이트
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            "에러: ${state.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiState.Empty -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(requireContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeAddCommentState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addCommentState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressIndicator.showLoading(true)
                    }

                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(requireContext(), "댓글 추가 성공!", Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            "댓글 추가 실패: ${state.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiState.Empty -> {
                        progressIndicator.showLoading(false)
                    }
                }
            }
        }
    }

    private fun setupSendButton(restaurantId: String) {
        binding.sendMsg.setOnClickListener {
            val content = binding.edittextMsg.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.addComment(restaurantId, content)
                binding.edittextMsg.text?.clear() // 입력창 초기화
            } else {
                Toast.makeText(requireContext(), "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
