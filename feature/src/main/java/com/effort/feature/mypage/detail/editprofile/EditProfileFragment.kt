package com.effort.feature.mypage.detail.editprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.effort.feature.R
import com.effort.feature.auth.AuthActivity
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.core.util.showToast
import com.effort.feature.databinding.FragmentEditprofileBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.auth.AuthViewModel
import com.effort.presentation.viewmodel.mypage.detail.editprofile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditprofileBinding>(FragmentEditprofileBinding::inflate) {
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val args: EditProfileFragmentArgs by navArgs() //SafeArgs로 데이터 받기
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditprofileBinding.inflate(inflater, container, false)
        initializePhotoPickerLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        observeUpdateNicknameState()
        observeCheckNicknameDuplicated()
        setupNicknameInputListener()
        saveButtonClickListener()
        setPhotoPickerClickListener()
        initializeUserProfile()

        signOutButtonClickListener()   // SignOut 클릭 리스너
        observeSignOutState()          // SignOut 상태 관찰
    }

    private fun initializeUserProfile() {
        with(binding) {
            // 닉네임 설정
            val nickname = args.nickname
            tagEdittextNickname.setText(nickname.ifEmpty { "" })


            // 프로필 사진 설정
            val profilePicUrl = args.profilePicUrl
            if (profilePicUrl.isNotEmpty()) {
                circularImageviewProfile.setImageUrl(profilePicUrl)
            } else {
                circularImageviewProfile.setImageUrl(R.drawable.profile_img_default)
            }
        }
    }

    private fun observeUpdateNicknameState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            editProfileViewModel.updateState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressIndicator.showLoading(true)
                    }

                    is UiState.Success -> {
                        progressIndicator.showLoading(false)
                        findNavController().navigateUp()
                    }

                    is UiState.Error -> {
                        progressIndicator.showLoading(false)
                        showToast(getString(R.string.update_failed_message))
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
            editProfileViewModel.checkNicknameDuplicatedState.collectLatest { state ->
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
                        // 닉네임 상태 초기화 (닉네임 입력 없음)
                        binding.nicknameStateMessage.text = ""
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
            text?.toString()?.trim()?.takeIf { it.isNotBlank() }
                ?.let { editProfileViewModel.checkNicknameDuplicated(it) }
                ?: editProfileViewModel.setEmptyNicknameState() // 비어있을 경우 Empty 상태로 전환
        }
    }

    private fun saveButtonClickListener() {
        binding.greenButtonSave.setOnClickListener {
            val newNickname = binding.tagEdittextNickname.getText().trim()
            val profilePictureUri = editProfileViewModel.selectedImageUri.value // 선택된 이미지의 URI

            if (newNickname.isNotBlank() || !profilePictureUri.isNullOrEmpty()) {
                editProfileViewModel.updateProfile(
                    nickname = newNickname.takeIf { it.isNotBlank() },
                    profilePictureUri = profilePictureUri
                )
            } else {
                showToast(getString(R.string.no_input_message))
            }
        }
    }

    // Activity Result Launcher 초기화
    private fun initializePhotoPickerLauncher() {
        photoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri: Uri? = result.data?.data
                    if (selectedImageUri != null) {
                        val imagePath = selectedImageUri.toString() // URI를 String으로 변환
                        editProfileViewModel.setSelectedImageUri(imagePath) // ViewModel로 전달
                        binding.circularImageviewProfile.setImageUri(selectedImageUri)
                    } else {
                        showToast(getString(R.string.need_select_image))
                    }
                }
            }
    }

    private fun launchPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        photoPickerLauncher.launch(photoPickerIntent) // Activity Result API 사용
    }

    private fun setPhotoPickerClickListener() {
        binding.circularImageviewEditProfile.setOnClickListener {
            launchPhotoPicker()
        }
    }

    private fun signOutButtonClickListener() {
        binding.signOut.setOnClickListener {
            authViewModel.signOut()
        }
    }

    private fun observeSignOutState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.signOutState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        // 로딩 중 UI 표시
                        progressIndicator.showLoading(true)
                    }

                    is UiState.Success -> {
                        // 로그아웃 성공 시 화면 전환
                        progressIndicator.showLoading(false)
                        navigateToAuthActivity()
                    }

                    is UiState.Error -> {
                        // 로그아웃 실패 시 메시지 표시
                        progressIndicator.showLoading(false)
                        showToast(getString(R.string.sign_out_failed_message))
                    }

                    is UiState.Empty -> {
                        progressIndicator.showLoading(false)
                    }
                }
            }
        }
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // 현재 Fragment가 있는 Activity 종료
    }
}
