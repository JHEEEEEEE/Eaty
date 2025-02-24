package com.effort.remote.service.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.effort.remote.model.location.LocationResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationService {

    /**
     * 현재 위치를 가져오는 함수
     * - 위치 권한이 없으면 null 반환
     * - 위치 정보를 성공적으로 가져오면 `LocationResponse`로 반환
     */
    override suspend fun getCurrentLocation(): LocationResponse? {
        Timber.i("getCurrentLocation() 호출됨")

        return suspendCancellableCoroutine { continuation ->
            try {
                // 1. 위치 권한 확인 (권한이 없으면 null 반환)
                if (!hasLocationPermission()) {
                    Timber.w("권한 없음: 위치 접근 권한이 없습니다.")
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                Timber.d("권한 확인 완료, 위치 요청 시작")

                // 2. 위치 요청 실행 (높은 정확도 우선)
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, null
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        Timber.i("위치 요청 성공 - 위도: ${location.latitude}, 경도: ${location.longitude}")

                        // 3. 성공 시 위경도 값을 LocationResponse에 담아서 반환
                        val locationResponse = LocationResponse(
                            latitude = location.latitude, longitude = location.longitude
                        )
                        continuation.resume(locationResponse)
                    } else {
                        Timber.w("위치 정보를 찾을 수 없음")
                        continuation.resume(null)
                    }
                }.addOnFailureListener { e ->
                    Timber.e(e, "위치 요청 실패")
                    continuation.resume(null)
                }
            } catch (e: SecurityException) {
                Timber.e(e, "보안 예외 발생")
                continuation.resume(null)
            }
        }
    }

    /**
     * 위치 접근 권한이 있는지 확인
     * - ACCESS_FINE_LOCATION 및 ACCESS_COARSE_LOCATION 권한 체크
     */
    private fun hasLocationPermission(): Boolean {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        Timber.d("위치 권한 체크 결과: $hasPermission")
        return hasPermission
    }
}