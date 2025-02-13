@file:Suppress("DEPRECATION")

package com.effort.feature.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.effort.feature.MainActivity
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.ActivityAuthBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("AuthActivity", "Google Sign-In 결과 수신")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    Log.d("AuthActivity", "Google Sign-In 성공: ${it.email}")
                    viewModel.handleSignInResult(it)
                }
            } catch (e: ApiException) {
                Log.e("AuthActivity", "Google Sign-In 실패: ${e.message}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeAuthenticatedState()
        Log.d("AuthActivity", "로그인 상태 확인 시작")
        viewModel.checkUserLoggedIn()
    }

    private fun setupUI() {
        binding.googleSignIn.setOnClickListener {
            Log.d("AuthActivity", "Google Sign-In 버튼 클릭")
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun observeAuthenticatedState() {
        val progressIndicator = binding.progressCircular.progressBar

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authenticateState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            Log.d("AuthActivity", "로딩 상태")
                            progressIndicator.showLoading(true)
                        }
                        is UiState.Success -> {
                            Log.d("AuthActivity", "로그인 성공")
                            progressIndicator.showLoading(false)
                            navigateToMainActivity()
                        }
                        is UiState.Error -> {
                            Log.e("AuthActivity", "로그인 실패: ${state.exception.message}")
                            progressIndicator.showLoading(false)
                        }
                        is UiState.Empty -> {
                            Log.d("AuthActivity", "빈 상태")
                            progressIndicator.showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        Log.d("AuthActivity", "메인 화면으로 이동")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}