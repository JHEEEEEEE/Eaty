package com.effort.feature

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.effort.feature.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Edge-to-Edge 활성화

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        setupStatusBar() // 상태바 색상 및 패딩 설정
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupStatusBar() {
        window.apply {
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.primary_color)
            WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = false
        }
        applyStatusBarPadding(binding.root)
    }

    private fun applyStatusBarPadding(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(
                v.paddingLeft,
                statusBarHeight,
                v.paddingRight,
                v.paddingBottom
            )
            insets
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            Log.d("DeepLink", "받은 딥링크 URI: $uri")

            val restaurantTitle = uri.getQueryParameter("title") ?: return
            val lotNumberAddress = uri.getQueryParameter("lotNumberAddress") ?: ""
            val roadNameAddress = uri.getQueryParameter("roadNameAddress") ?: ""
            val distance = uri.getQueryParameter("distance") ?: ""
            val phoneNumber = uri.getQueryParameter("phoneNumber") ?: ""
            val placeUrl = uri.getQueryParameter("placeUrl") ?: ""

            Log.d("DeepLink", "딥링크 데이터 - Title: $restaurantTitle")

            navigateToRestaurantDetail(
                restaurantTitle, lotNumberAddress, roadNameAddress,
                distance, phoneNumber, placeUrl
            )
        }
    }

    private fun navigateToRestaurantDetail(
        restaurantTitle: String,
        lotNumberAddress: String,
        roadNameAddress: String,
        distance: String,
        phoneNumber: String,
        placeUrl: String
    ) {
        Log.d("DeepLink", "navigateToRestaurantDetail() 실행됨, title: $restaurantTitle")

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val bundle = Bundle().apply {
                    putString("title", restaurantTitle)
                    putString("lotNumberAddress", lotNumberAddress)
                    putString("roadNameAddress", roadNameAddress)
                    putString("distance", distance)
                    putString("phoneNumber", phoneNumber)
                    putString("placeUrl", placeUrl)
                }

                Log.d("DeepLink", "navController로 이동: title=$restaurantTitle, address=$lotNumberAddress")

                navController.navigate(R.id.restaurantDetailFragment, bundle) // ✅ SafeArgs 없이 이동
            } catch (e: Exception) {
                Log.e("DeepLink", "Navigation 오류: ${e.message}")
            }
        }
    }


}