package com.effort.feature.mypage.detail.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentFaqBinding
import com.effort.feature.databinding.FragmentQuestionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAQFragment :
    BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {
    override fun initView() {

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