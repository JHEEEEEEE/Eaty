package com.effort.feature.mypage.detail.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentEditprofileBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.mypage.detail.editprofile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditprofileBinding>(FragmentEditprofileBinding::inflate) {
    private val viewModel: EditProfileViewModel by viewModels()

    override fun initView() {
        observeUpdateNicknameState()
        observeCheckNicknameDuplicated()
        setupNicknameInputListener()
        saveButtonClickListener()
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
        _binding = FragmentEditprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeUpdateNicknameState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateNicknameState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressIndicator.showLoading(true)
                    }

                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        findNavController().navigateUp()  // 수정필요 -> mypage로 back
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

    private fun observeCheckNicknameDuplicated() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.checkNicknameDuplicatedState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.greenButtonSave.isEnabled = false // 로딩 중 비활성화
                    }

                    is UiState.Success -> {
                        updateNicknameStatus(state.data) // 닉네임 중복 여부에 따른 상태 업데이트
                    }

                    is UiState.Error -> {
                        showErrorStatus() // 오류 발생 시 상태 업데이트
                    }

                    is UiState.Empty -> {
                        binding.greenButtonSave.isEnabled = false // 초기 상태 비활성화
                    }
                }
            }
        }
    }

    private fun updateNicknameStatus(isAvailable: Boolean) {
        with(binding) {
            greenButtonSave.isEnabled = isAvailable
            nicknameStateMessage.apply {
                text = getString(
                    if (isAvailable) R.string.nickname_available_message
                    else R.string.nickname_duplicate_message
                )
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isAvailable) R.color.primary_color
                        else R.color.error_color
                    )
                )
            }
        }
    }

    private fun showErrorStatus() {
        with(binding.nicknameStateMessage) {
            text = getString(R.string.nickname_error_message)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.error_color))
        }
        binding.greenButtonSave.isEnabled = false
    }

    // 닉네임 입력 리스너 설정 함수
    private fun setupNicknameInputListener() {
        binding.tagEdittextNickname.doAfterTextChanged { text ->
            text?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let {
                viewModel.checkNicknameDuplicated(it)
            }
        }
    }

    private fun saveButtonClickListener() {
        binding.greenButtonSave.setOnClickListener {
            binding.tagEdittextNickname.getText().trim().takeIf { it.isNotBlank() }
                ?.let { newNickname ->
                    viewModel.updateNickname(newNickname) // ViewModel에서 닉네임 업데이트
                } ?: Toast.makeText(
                requireContext(),
                getString(R.string.nickname_empty_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
