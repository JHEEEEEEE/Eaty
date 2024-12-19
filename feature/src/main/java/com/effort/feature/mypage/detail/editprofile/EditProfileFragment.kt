package com.effort.feature.mypage.detail.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
        saveButtonClickListener()
        observeUpdateNicknameState()
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


    private fun saveButtonClickListener() {
        binding.greenButtonSave.setOnClickListener {
            val newNickname = binding.tagEdittextNickname.getText().trim()

            // 닉네임이 변경되었는지 확인
            if (newNickname.isNotBlank()) {
                viewModel.updateNickname(newNickname) // ViewModel에서 닉네임 업데이트
            } else {
                Toast.makeText(requireContext(), "닉네임이 입력되지 않았음.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}