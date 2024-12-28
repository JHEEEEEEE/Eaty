package com.effort.feature.mypage.detail.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentNoticeBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.mypage.detail.notice.NoticeViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticeFragment: BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    private val viewModel: NoticeViewModel by viewModels()
    private lateinit var noticeListAdapter: NoticeListAdapter
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun initView() {
        noticeListAdapter = NoticeListAdapter()
        progressIndicator = binding.progressCircular.progressBar

        binding.recyclerviewNotice.apply {
            adapter = noticeListAdapter
        }

        // ViewModel의 상태를 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getNoticeState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressIndicator.visibility = View.VISIBLE
                            binding.recyclerviewNotice.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            progressIndicator.visibility = View.GONE
                            binding.recyclerviewNotice.visibility = View.VISIBLE
                            noticeListAdapter.submitList(state.data)
                        }

                        is UiState.Error -> {
                            progressIndicator.visibility = View.GONE
                            binding.recyclerviewNotice.visibility = View.GONE

                            showToast(getString(R.string.error, state.exception.message))
                        }

                        is UiState.Empty -> {
                            progressIndicator.visibility = View.GONE
                            binding.recyclerviewNotice.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    // 실행테스트 더미코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }
}