@file:Suppress("DEPRECATION")

package com.effort.feature.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    // Google 로그인 결과 처리 (ActivityResult API 활용)
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("AuthActivity", "Google Sign-In 결과 수신")

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    Log.d("AuthActivity", "Google Sign-In 성공: ${it.email}")

                    // 로그인 성공 시 서버 인증을 위해 ViewModel에 계정 정보 전달
                    viewModel.handleSignInResult(it)
                }
            } catch (e: ApiException) {
                Log.e("AuthActivity", "Google Sign-In 실패: ${e.message}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 시스템 UI를 확장하여 전체 화면 적용
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeAuthenticatedState()
        Log.d("AuthActivity", "로그인 상태 확인 시작")

        // 앱 실행 시 자동 로그인 여부 확인
        viewModel.checkUserLoggedIn()
    }

    /**
     * Google 로그인 버튼 클릭 시 로그인 화면
     */
    private fun setupUI() {
        binding.googleSignIn.setOnClickListener {
            Log.d("AuthActivity", "Google Sign-In 버튼 클릭")
            googleSignInLauncher.launch(viewModel.getGoogleSignInIntent())
        }
    }

    /**
     * 로그인 상태를 감지하여 인증 완료 시 메인 화면으로 이동
     */
    private fun observeAuthenticatedState() {
        observeStateLatestWithLifecycleOnActivity(
            stateFlow = viewModel.authenticateState,
            progressView = binding.progressCircular.progressBar,
            activity = this,
            onSuccess = { navigateToMainActivity() }
        )
    }

    /**
     * 로그인 성공 시 메인 화면으로 이동하고 현재 로그인 화면을 종료
     */
    private fun navigateToMainActivity() {
        Log.d("AuthActivity", "메인 화면으로 이동")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}