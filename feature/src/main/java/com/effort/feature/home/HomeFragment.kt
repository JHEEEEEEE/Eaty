package com.effort.feature.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentHomeBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.mypage.MyPageViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var progressIndicator: ProgressBar

    override fun initView() {
        Log.d(TAG, "Initializing view") // Log for initView()
        // FirebaseAuth 인스턴스 가져오기
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        viewModel.observeUserUpdate(currentUser?.email?:"")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        Log.d(TAG, "onCreateView: Binding initialized")
        return binding.root
    }

    // 실행 테스트 더미 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ViewModel initialized: $viewModel") // Log ViewModel initialization
        initView()
        observeUserUpdates()
    }

    private fun observeUserUpdates() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userUpdateState.collectLatest { state ->
                    Log.d(TAG, "Received state: $state") // Log received state
                    when (state) {
                        is UiState.Loading -> {
                            Log.d(TAG, "State: Loading")
                            showLoading(true)
                        }
                        is UiState.Success -> {
                            Log.d(TAG, "State: Success, User Name: ${state.data.name}")
                            showLoading(false)
                            binding.userName.text = state.data.name
                        }
                        is UiState.Error -> {
                            Log.e(TAG, "State: Error, Message: ${state.exception.message}")
                            showLoading(false)
                        }
                        is UiState.Empty -> {
                            Log.d(TAG, "State: Empty")
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressIndicator = binding.progressCircular.progressBar
        Log.d(TAG, "Updating loading indicator: isLoading = $isLoading")
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
