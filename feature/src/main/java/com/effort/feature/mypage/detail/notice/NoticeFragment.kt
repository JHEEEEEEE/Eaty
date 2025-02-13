package com.effort.feature.mypage.detail.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatestWithLifecycle
import com.effort.feature.databinding.FragmentNoticeBinding
import com.effort.presentation.viewmodel.mypage.detail.notice.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    private val viewModel: NoticeViewModel by viewModels()
    private lateinit var noticeListAdapter: NoticeListAdapter

    override fun initView() {

        setupRecyclerView()
        observeViewModel()
    }

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

    private fun setupRecyclerView() {
        noticeListAdapter = NoticeListAdapter()

        binding.recyclerviewNotice.apply {
            adapter = noticeListAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        observeStateLatestWithLifecycle(
            stateFlow = viewModel.getNoticeState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { noticeData ->
            noticeListAdapter.submitList(noticeData) // 공지사항 목록 업데이트
        }
    }
}