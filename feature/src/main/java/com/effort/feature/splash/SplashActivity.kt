package com.effort.feature.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.effort.feature.auth.AuthActivity
import com.effort.feature.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToAuthAfterDelay()
    }

    /**
     * 일정 시간 후 인증 화면(AuthActivity)으로 이동
     * - 2초 후 자동으로 이동
     * - 이동 후 현재 액티비티 종료
     */
    private fun navigateToAuthAfterDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}