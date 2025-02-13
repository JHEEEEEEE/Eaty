package com.effort.feature.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.observeStateLatestWithLifecycle
import com.effort.feature.databinding.FragmentMypageBinding
import com.effort.presentation.model.auth.FirebaseUserModel
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    private val viewModel: MyPageViewModel by viewModels()

    private lateinit var profilePicUrl: String
    private lateinit var nickname: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        observeViewModel()
        setNavigationClickListener()
    }


    // 실행테스트 더미코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun observeViewModel() {
        observeStateLatestWithLifecycle(
            stateFlow = viewModel.userUpdateState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { userData ->
            updateUIWithUserProfile(userData) // ✅ UI 업데이트
            profilePicUrl = userData.profilePicPath // ✅ 프로필 사진 업데이트
            nickname = userData.nickname // ✅ 닉네임 업데이트
        }
    }


    private fun updateUIWithUserProfile(userProfile: FirebaseUserModel?) {
        with(binding) {
            profileName.text = getDisplayName(userProfile)
            profileEmail.text = userProfile?.email ?: getString(R.string.needLogin)
            loadProfileImage(userProfile?.profilePicPath)
        }
    }

    private fun getDisplayName(userProfile: FirebaseUserModel?): String {
        return when {
            userProfile == null -> getString(R.string.guest)
            userProfile.nickname.isNotEmpty() -> userProfile.nickname
            else -> userProfile.name
        }
    }

    private fun loadProfileImage(profilePicPath: String?) {
        if (!profilePicPath.isNullOrEmpty()) {
            Glide.with(binding.profileImage.context)
                .load(profilePicPath)
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
            binding.notice to R.id.action_myPageFragment_to_noticeFragment
        )

        navigationMap.forEach { (view, actionId) ->
            view.setOnClickListener {
                findNavController().navigate(actionId)
            }
        }

        // profileSettings 클릭 리스너에 데이터 전달 로직 추가
        binding.profileSettings.setOnClickListener {
            val action = MyPageFragmentDirections.actionMyPageFragmentToEditProfileFragment(
                profilePicUrl = profilePicUrl,
                nickname = nickname
            )
            findNavController().navigate(action)
        }
    }
}