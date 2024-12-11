package com.effort.feature.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.effort.feature.R
import com.effort.feature.community.giveaway.GiveAwayFragment
import com.effort.feature.community.question.QuestionFragment
import com.effort.feature.community.meetup.MeetupFragment
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout

class CommunityFragment :
    BaseFragment<FragmentCommunityBinding>(FragmentCommunityBinding::inflate) {
    override fun initView() {

    }

    // 실행테스트 더미코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)

        // 탭 추가
        binding.tabs?.apply {
            addTab(this.newTab().setText("게시글"))
            addTab(this.newTab().setText("나눔"))
            addTab(this.newTab().setText("모임"))
        }

        // 기본으로 첫 번째 탭의 Fragment 로드
        loadFragment(QuestionFragment())

        // 탭 선택 시 프래그먼트 변경
        binding.tabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "게시글" -> loadFragment(QuestionFragment())
                    "나눔" -> loadFragment(GiveAwayFragment())
                    "모임" -> loadFragment(MeetupFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        return binding.root
    }

    // 프래그먼트 로드 함수
    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.tabContent, fragment)
            .commit()
    }
}