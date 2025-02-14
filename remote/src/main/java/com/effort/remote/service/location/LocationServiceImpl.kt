package com.effort.remote.service.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.effort.remote.model.location.LocationResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationService {

    override suspend fun getCurrentLocation(): LocationResponse? {
        Log.d("LocationServiceImpl", "getCurrentLocation() 호출됨")

        return suspendCancellableCoroutine { continuation ->
            try {
                // 1. 위치 권한 확인
                if (!hasLocationPermission()) {
                    Log.e("LocationServiceImpl", "권한 없음: 위치 접근 권한이 없습니다.")
                    continuation.resume(null) // 권한 없을 경우 null 반환
                    return@suspendCancellableCoroutine
                }

                Log.d("LocationServiceImpl", "권한 확인 완료, 위치 요청 시작")

                // 2. 위치 요청 실행
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, // 높은 정확도 우선
                    null // 위치 업데이트 제한 없음
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d("LocationServiceImpl", "위치 요청 성공: $location")

                        // 3. 성공 시 위경도 값을 LocationResponse에 담아서 반환
                        val locationResponse = LocationResponse(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        continuation.resume(locationResponse)
                    } else {
                        Log.e("LocationServiceImpl", "위치 정보를 찾을 수 없음")
                        continuation.resume(null) // 위치 정보가 없을 경우 null 반환
                    }
                }.addOnFailureListener { e ->
                    Log.e("LocationServiceImpl", "위치 요청 실패: ${e.message}")
                    continuation.resume(null) // 실패 시 null 반환
                }
            } catch (e: SecurityException) {
                Log.e("LocationServiceImpl", "보안 예외 발생: ${e.message}")
                continuation.resume(null) // 예외 발생 시 null 반환
            }
        }
    }

    // 권한 체크 함수 - 위치 접근 권한이 있는지 확인
    private fun hasLocationPermission(): Boolean {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        Log.d("LocationServiceImpl", "위치 권한 체크 결과: $hasPermission")
        return hasPermission
    }
}
