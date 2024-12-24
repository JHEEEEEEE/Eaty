package com.effort.feature.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentHomeBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel: MyPageViewModel by viewModels()

    override fun initView() {
        observeUserUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun observeUserUpdates() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userUpdateState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressIndicator.showLoading(true)
                        }
                        is UiState.Success -> {
                            progressIndicator.showLoading(false)
                            binding.userName.text = getDisplayName(state.data.nickname, state.data.name)
                        }
                        is UiState.Error -> {
                            progressIndicator.showLoading(false)
                        }
                        is UiState.Empty -> {
                            progressIndicator.showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun getDisplayName(nickname: String, name: String): String {
        return nickname.ifBlank { name }
    }
}
