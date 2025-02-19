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
import com.effort.feature.core.util.observeStateLatestWithLifecycleOnFragment
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * UI 초기화
     * - ViewModel 상태 관찰
     * - 네비게이션 클릭 리스너 설정
     */
    override fun initView() {
        observeViewModel()
        setNavigationClickListener()
    }

    /**
     * ViewModel에서 사용자 정보를 감지하여 UI 업데이트
     * - API 호출 상태를 감지하여 로딩 UI 처리
     * - 성공 시 사용자 프로필 및 닉네임 업데이트
     */
    private fun observeViewModel() {
        observeStateLatestWithLifecycleOnFragment(
            stateFlow = viewModel.userUpdateState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { userData ->
            updateUIWithUserProfile(userData)
            profilePicUrl = userData.profilePicPath
            nickname = userData.nickname
        }
    }

    /**
     * 사용자 정보로 UI 업데이트
     * - 사용자 이름과 이메일 표시
     * - 프로필 사진 로드
     */
    private fun updateUIWithUserProfile(userProfile: FirebaseUserModel?) {
        with(binding) {
            profileName.text = getDisplayName(userProfile)
            profileEmail.text = userProfile?.email ?: getString(R.string.needLogin)
            loadProfileImage(userProfile?.profilePicPath)
        }
    }

    /**
     * 사용자 이름을 반환
     * - 닉네임이 있으면 닉네임 반환
     * - 없으면 실명 반환
     * - 로그인하지 않았다면 "게스트" 반환
     */
    private fun getDisplayName(userProfile: FirebaseUserModel?): String {
        return when {
            userProfile == null -> getString(R.string.guest)
            userProfile.nickname.isNotEmpty() -> userProfile.nickname
            else -> userProfile.name
        }
    }

    /**
     * 사용자 프로필 이미지를 로드
     * - 이미지가 있으면 Glide로 로드
     * - 없으면 기본 이미지 설정
     */
    private fun loadProfileImage(profilePicPath: String?) {
        if (!profilePicPath.isNullOrEmpty()) {
            Glide.with(binding.profileImage.context).load(profilePicPath)
                .placeholder(R.drawable.placeholder_image).error(R.drawable.error_image)
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.profile_img_default)
        }
    }

    /**
     * 네비게이션 버튼 클릭 리스너 설정
     * - FAQ 및 공지사항 화면으로 이동
     * - 프로필 설정 클릭 시 현재 프로필 데이터를 전달
     */
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

        binding.profileSettings.setOnClickListener {
            val action = MyPageFragmentDirections.actionMyPageFragmentToEditProfileFragment(
                profilePicUrl = profilePicUrl, nickname = nickname
            )
            findNavController().navigate(action)
        }
    }
}