package com.effort.feature.community.giveaway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentGiveawayBinding

class GiveAwayFragment : BaseFragment<FragmentGiveawayBinding>(FragmentGiveawayBinding::inflate) {
    override fun initView() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGiveawayBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
}