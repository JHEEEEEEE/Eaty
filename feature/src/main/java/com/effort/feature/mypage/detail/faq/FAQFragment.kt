package com.effort.feature.mypage.detail.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.effort.feature.core.base.BaseFragment
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
    private lateinit var progressIndicator: View

    override fun initView() {
        faqListAdapter = FaqListAdapter()
        progressIndicator = binding.progressCircular.progressBar

        binding.recyclerviewFaq.apply {
            adapter = faqListAdapter
        }

        // ViewModel의 상태를 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        progressIndicator.visibility = View.VISIBLE
                        binding.recyclerviewFaq.visibility = View.GONE
                    }

                    is UiState.Success -> {
                        progressIndicator.visibility = View.GONE
                        binding.recyclerviewFaq.visibility = View.VISIBLE
                        faqListAdapter.submitList(uiState.data)
                    }

                    is UiState.Error -> {
                        progressIndicator.visibility = View.GONE
                        binding.recyclerviewFaq.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error: ${uiState.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiState.Empty -> {
                        progressIndicator.visibility = View.GONE
                        binding.recyclerviewFaq.visibility = View.GONE
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT)
                            .show()
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
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }
}