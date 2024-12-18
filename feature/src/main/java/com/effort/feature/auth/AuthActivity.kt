@file:Suppress("DEPRECATION")

package com.effort.feature.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.effort.feature.MainActivity
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
    private lateinit var progressIndicator: ProgressBar

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    viewModel.handleSignInResult(it)
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In 실패: ${e.message}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeState()

        // 로그인 상태 확인
        viewModel.checkUserLoggedIn()
    }

    private fun setupUI() {
        binding.googleSignIn.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authenticateState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> showLoading(true)
                        is UiState.Success -> {
                            showLoading(false)
                            navigateToMainActivity()
                        }

                        is UiState.Error -> {
                            showLoading(false)
                            Log.e(TAG, "Error: ${state.exception.message}")
                        }

                        is UiState.Empty -> showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressIndicator = binding.progressCircular.progressBar
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "AuthActivity"
    }
}