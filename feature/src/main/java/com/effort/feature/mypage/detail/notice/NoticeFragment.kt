package com.effort.feature.mypage.detail.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatestWithLifecycleOnFragment
import com.effort.feature.databinding.FragmentNoticeBinding
import com.effort.presentation.viewmodel.mypage.detail.notice.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    private val viewModel: NoticeViewModel by viewModels()
    private lateinit var noticeListAdapter: NoticeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 초기 UI 설정
     * - RecyclerView 설정
     * - ViewModel 상태 관찰
     */
    override fun initView() {
        setupRecyclerView()
        observeViewModel()
    }

    /**
     * 공지사항 목록을 표시하는 RecyclerView 초기화
     */
    private fun setupRecyclerView() {
        noticeListAdapter = NoticeListAdapter()

        binding.recyclerviewNotice.apply {
            adapter = noticeListAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * ViewModel에서 공지사항 데이터를 감지하여 UI 업데이트
     * - API 호출 상태를 감지하여 로딩 UI 처리
     * - 성공 시 RecyclerView 목록 업데이트
     */
    private fun observeViewModel() {
        observeStateLatestWithLifecycleOnFragment(
            stateFlow = viewModel.getNoticeState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { noticeData ->
            noticeListAdapter.submitList(noticeData)
        }
    }
}