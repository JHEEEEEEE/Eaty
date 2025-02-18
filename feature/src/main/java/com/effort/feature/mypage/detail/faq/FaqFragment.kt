package com.effort.feature.mypage.detail.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatestWithLifecycleOnFragment
import com.effort.feature.databinding.FragmentFaqBinding
import com.effort.presentation.viewmodel.mypage.detail.faq.FaqViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaqFragment :
    BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {

    private val viewModel: FaqViewModel by viewModels()
    private lateinit var faqListAdapter: FaqListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 초기 UI 설정
     * - RecyclerView 설정
     * - FAQ 데이터 상태 관찰
     */
    override fun initView() {
        initRecyclerView()
        observeGetFaqState()
    }

    /**
     * FAQ 목록을 표시하는 RecyclerView 초기화
     */
    private fun initRecyclerView() {
        faqListAdapter = FaqListAdapter()
        binding.recyclerviewFaq.adapter = faqListAdapter
    }

    /**
     * ViewModel에서 FAQ 데이터를 가져와 UI 업데이트
     * - API 호출 상태를 감지하여 로딩 UI 처리
     * - 성공 시 RecyclerView 목록 업데이트
     */
    private fun observeGetFaqState() {
        observeStateLatestWithLifecycleOnFragment(
            stateFlow = viewModel.getFaqState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { faqData ->
            faqListAdapter.submitList(faqData)
        }
    }
}