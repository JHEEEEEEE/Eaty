package com.effort.feature.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentMypageBinding
import com.effort.presentation.UiState
import com.effort.presentation.model.auth.FirebaseUserModel
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        observeUserUpdates()
        setNavigationClickListener()
    }


    // 실행테스트 더미코드
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
                            updateUIWithUserProfile(state.data)
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

    private fun updateUIWithUserProfile(userProfile: FirebaseUserModel?) {
        with(binding) {
            profileName.text = getDisplayName(userProfile)
            profileEmail.text = userProfile?.email ?: getString(R.string.needLogin)
            loadProfileImage(userProfile?.profilePicUrl)
        }
    }

    private fun getDisplayName(userProfile: FirebaseUserModel?): String {
        return when {
            userProfile == null -> getString(R.string.guest)
            userProfile.nickname.isNotEmpty() -> userProfile.nickname
            else -> userProfile.name
        }
    }

    private fun loadProfileImage(profilePicUrl: String?) {
        if (!profilePicUrl.isNullOrEmpty()) {
            Glide.with(binding.profileImage.context)
                .load(profilePicUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.profile_img_default)
        }
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