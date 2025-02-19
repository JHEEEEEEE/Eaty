@file:Suppress("DEPRECATION")

package com.effort.feature.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.effort.feature.MainActivity
import com.effort.feature.core.util.observeStateLatestWithLifecycleOnActivity
import com.effort.feature.databinding.ActivityAuthBinding
import com.effort.presentation.viewmodel.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    // Google 로그인 결과 처리 (ActivityResult API 활용)
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    // 로그인 성공 시 서버 인증을 위해 ViewModel에 계정 정보 전달
                    viewModel.handleSignInResult(it)
                }
            } catch (e: ApiException) {
                Timber.e("Google Sign-In 실패: ${e.message}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeAuthenticatedState()

        viewModel.checkUserLoggedIn()
    }

    /**
     * Google 로그인 버튼 클릭 시 로그인 화면
     */
    private fun setupUI() {
        binding.googleSignIn.setOnClickListener {
            googleSignInLauncher.launch(viewModel.getGoogleSignInIntent())
        }
    }

    /**
     * 로그인 상태를 감지하여 인증 완료 시 메인 화면으로 이동
     */
    private fun observeAuthenticatedState() {
        observeStateLatestWithLifecycleOnActivity(stateFlow = viewModel.authenticateState,
            progressView = binding.progressCircular.progressBar,
            activity = this,
            onSuccess = { navigateToMainActivity() })
    }

    /**
     * 로그인 성공 시 메인 화면으로 이동하고 현재 로그인 화면을 종료
     */
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}