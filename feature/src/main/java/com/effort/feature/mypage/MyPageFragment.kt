package com.effort.feature.mypage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentMypageBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        setNavigationClickListener()
    }


    // 실행테스트 더미코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    // 공통 네비게이션 클릭 리스너 메소드
    private fun setNavigationClickListener() {
        val navigationMap = mapOf(
            binding.faq to R.id.action_myPageFragment_to_FAQFragment,
            binding.notice to R.id.action_myPageFragment_to_noticeFragment,
            binding.profileSettings to R.id.action_myPageFragment_to_editProfileFragment,
        )

        navigationMap.forEach { (view, actionId) ->
            view.setOnClickListener {
                    findNavController().navigate(actionId)
                }
            }
        }
    }