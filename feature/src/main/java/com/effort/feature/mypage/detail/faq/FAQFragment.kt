package com.effort.feature.mypage.detail.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentFaqBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.mypage.detail.faq.FaqViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FAQFragment :
    BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {

    private val viewModel: FaqViewModel by viewModels()
    private lateinit var faqListAdapter: FaqListAdapter

    override fun initView() {
        initRecyclerView()
        observeGetFaqState()
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
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getFaqState.collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            progressIndicator.showLoading(true)
                            binding.recyclerviewFaq.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewFaq.visibility = View.VISIBLE
                            faqListAdapter.submitList(uiState.data)
                        }

                        is UiState.Error -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewFaq.visibility = View.GONE
                        }

                        is UiState.Empty -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewFaq.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}