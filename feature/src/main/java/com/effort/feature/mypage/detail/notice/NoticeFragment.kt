package com.effort.feature.mypage.detail.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentRecyclerviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment: BaseFragment<FragmentRecyclerviewBinding>(FragmentRecyclerviewBinding::inflate) {
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
        _binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)
        return binding.root
    }
}