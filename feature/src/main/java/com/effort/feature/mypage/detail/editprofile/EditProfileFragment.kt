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
import com.effort.feature.core.util.observeStateLatest
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
    private val args: EditProfileFragmentArgs by navArgs()
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditprofileBinding.inflate(inflater, container, false)
        initializePhotoPickerLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        observeViewModelState()
        observeCheckNicknameDuplicated()
        setupNicknameInputListener()
        saveButtonClickListener()
        setPhotoPickerClickListener()
        initializeUserProfile()
        signOutButtonClickListener()
    }

    /**
     * 기존 사용자 프로필 데이터를 UI에 반영
     * - SafeArgs를 통해 전달된 닉네임과 프로필 사진 URL을 사용
     */
    private fun initializeUserProfile() {
        with(binding) {
            tagEdittextNickname.setText(args.nickname.ifEmpty { "" })
            val profilePicUrl = args.profilePicUrl
            circularImageviewProfile.setImageUrl(
                profilePicUrl.ifEmpty { R.drawable.profile_img_default }
            )
        }
    }

    /**
     * ViewModel의 상태를 관찰하여 UI를 업데이트
     * - 프로필 수정 성공 여부
     * - 로그아웃 성공 여부
     */
    private fun observeViewModelState() {
        observeStateLatest(
            stateFlow = editProfileViewModel.updateState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { success ->
            if (success) findNavController().navigateUp()
            else showToast(getString(R.string.update_failed_message))
        }

        observeStateLatest(
            stateFlow = authViewModel.signOutState,
            progressView = binding.progressCircular.progressBar,
            fragment = this
        ) { success ->
            if (success) navigateToAuthActivity()
            else showToast(getString(R.string.sign_out_failed_message))
        }
    }

    /**
     * 닉네임 중복 검사 결과를 UI에 반영
     * - 중복 검사 중이면 저장 버튼 비활성화
     * - 중복 검사 결과에 따라 저장 가능 여부 업데이트
     */
    private fun observeCheckNicknameDuplicated() {
        viewLifecycleOwner.lifecycleScope.launch {
            editProfileViewModel.checkNicknameDuplicatedState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> binding.greenButtonSave.isEnabled = false
                    is UiState.Success -> updateNicknameStatus(state.data)
                    is UiState.Error -> showErrorStatus()
                    is UiState.Empty -> binding.nicknameStateMessage.text = ""
                }
            }
        }
    }

    /**
     * 닉네임 중복 여부에 따라 UI 상태 업데이트
     * - 사용 가능하면 초록색 표시, 불가능하면 빨간색 표시
     */
    private fun updateNicknameStatus(isAvailable: Boolean) {
        with(binding) {
            greenButtonSave.isEnabled = isAvailable
            nicknameStateMessage.apply {
                text = getString(if (isAvailable) R.string.nickname_available_message else R.string.nickname_duplicate_message)
                setTextColor(ContextCompat.getColor(requireContext(), if (isAvailable) R.color.primary_color else R.color.error_color))
            }
        }
    }

    /**
     * 닉네임 중복 검사 중 오류 발생 시 UI 업데이트
     * - 오류 메시지 표시 후 저장 버튼 비활성화
     */
    private fun showErrorStatus() {
        with(binding.nicknameStateMessage) {
            text = getString(R.string.nickname_error_message)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.error_color))
        }
        binding.greenButtonSave.isEnabled = false
    }

    /**
     * 닉네임 입력 변경 감지 후 중복 검사 수행
     * - 닉네임이 입력되면 중복 검사 요청
     * - 입력이 비어 있으면 상태 초기화
     */
    private fun setupNicknameInputListener() {
        binding.tagEdittextNickname.doAfterTextChanged { text ->
            text?.toString()?.trim()?.takeIf { it.isNotBlank() }
                ?.let { editProfileViewModel.checkNicknameDuplicated(it) }
                ?: editProfileViewModel.setEmptyNicknameState()
        }
    }

    /**
     * 저장 버튼 클릭 시 프로필 업데이트 요청
     * - 닉네임이 변경되었거나 프로필 사진이 선택되었을 경우만 업데이트 실행
     */
    private fun saveButtonClickListener() {
        binding.greenButtonSave.setOnClickListener {
            val newNickname = binding.tagEdittextNickname.getText().trim()
            val profilePictureUri = editProfileViewModel.selectedImageUri.value

            if (newNickname.isNotBlank() || !profilePictureUri.isNullOrEmpty()) {
                editProfileViewModel.updateProfile(
                    nickname = newNickname.takeIf { it.isNotBlank() },
                    profilePictureUri = profilePictureUri
                )
            } else showToast(getString(R.string.no_input_message))
        }
    }

    /**
     * 갤러리에서 사진을 선택하는 Activity Result Launcher 초기화
     * - 사용자가 사진을 선택하면 ViewModel에 저장
     */
    private fun initializePhotoPickerLauncher() {
        photoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    val imagePath = selectedImageUri.toString()
                    editProfileViewModel.setSelectedImageUri(imagePath)
                    binding.circularImageviewProfile.setImageUri(selectedImageUri)
                } else showToast(getString(R.string.need_select_image))
            }
        }
    }

    /**
     * 갤러리에서 사진을 선택하는 Intent 실행
     */
    private fun launchPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        photoPickerLauncher.launch(photoPickerIntent)
    }

    /**
     * 프로필 사진 변경 버튼 클릭 시 사진 선택 기능 실행
     */
    private fun setPhotoPickerClickListener() {
        binding.circularImageviewEditProfile.setOnClickListener {
            launchPhotoPicker()
        }
    }

    /**
     * 로그아웃 버튼 클릭 시 로그아웃 요청
     */
    private fun signOutButtonClickListener() {
        binding.signOut.setOnClickListener {
            authViewModel.signOut()
        }
    }

    /**
     * 로그아웃 성공 시 로그인 화면(AuthActivity)으로 이동
     * - 기존 화면을 종료하고 로그인 화면을 새로 시작
     */
    private fun navigateToAuthActivity() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}