package com.effort.feature.mypage.detail.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatestWithLifecycle
import com.effort.feature.databinding.FragmentFaqBinding
import com.effort.presentation.viewmodel.mypage.detail.faq.FaqViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaqFragment :
    BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {

    private val viewModel: FaqViewModel by viewModels()
    private lateinit var faqListAdapter: FaqListAdapter

    override fun initView() {
        initRecyclerView()
        observeGetFaqState()
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
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initRecyclerView() {
        faqListAdapter = FaqListAdapter()
        binding.recyclerviewFaq.apply {
            adapter = faqListAdapter
        }
    }

    private fun observeGetFaqState() {
        observeStateLatestWithLifecycle(
            stateFlow = viewModel.getFaqState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { faqData ->
            faqListAdapter.submitList(faqData)
        }
    }
}