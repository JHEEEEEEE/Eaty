package com.effort.remote.service.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationService {

    override suspend fun getCurrentLocation(): Location? {
        Log.d("LocationServiceImpl", "getCurrentLocation() 호출됨")
        return suspendCancellableCoroutine { continuation ->
            try {
                if (!hasLocationPermission()) {
                    Log.e("LocationServiceImpl", "권한 없음: 위치 접근 권한이 없습니다.")
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                Log.d("LocationServiceImpl", "권한 확인 완료, 위치 요청 시작")

                // 위치 요청 설정 - 높은 정확도
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, // 정확도 최우선
                    10000 // 10초 타임아웃 설정
                ).apply {
                    setMinUpdateIntervalMillis(5000) // 최소 업데이트 주기 5초
                    setMaxUpdateDelayMillis(10000) // 최대 지연 시간 10초
                }.build()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, // 정확도 최우선
                    null // 위치 업데이트를 위한 제한 조건 없음
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d("LocationServiceImpl", "위치 요청 성공: $location")
                    } else {
                        Log.e("LocationServiceImpl", "위치 정보를 찾을 수 없음")
                    }
                    continuation.resume(location)
                }.addOnFailureListener { e ->
                    Log.e("LocationServiceImpl", "위치 요청 실패: ${e.message}")
                    continuation.resume(null)
                }
            } catch (e: SecurityException) {
                Log.e("LocationServiceImpl", "보안 예외 발생: ${e.message}")
                continuation.resume(null)
            }
        }
    }

    // 위치 업데이트 Flow 제공 함수 - 실시간 위치 업데이트를 스트림으로 전달
    override fun getLocationUpdates(): Flow<Location> = callbackFlow {
        Log.d("LocationServiceImpl", "getLocationUpdates() 호출됨")
        try {
            // 권한 체크
            if (!hasLocationPermission()) {
                Log.e("LocationServiceImpl", "권한 없음: 위치 접근 권한이 없습니다.")
                close(SecurityException("Location permission is not granted"))
                return@callbackFlow
            }

            Log.d("LocationServiceImpl", "권한 확인 완료, 위치 업데이트 요청 시작")

            // 위치 요청 설정
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000
            ).apply {
                setMinUpdateIntervalMillis(2000)
                setMaxUpdateDelayMillis(10000)
            }.build()

            Log.d("LocationServiceImpl", "위치 요청 설정 완료: $locationRequest")

            // 위치 업데이트 콜백 정의
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    Log.d("LocationServiceImpl", "위치 업데이트 수신: $location")
                    location?.let { trySend(it) }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        Log.e("LocationServiceImpl", "위치 가용 불가")
                    } else {
                        Log.d("LocationServiceImpl", "위치 가용 가능")
                    }
                }
            }

            // 초기 위치 전송
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("LocationServiceImpl", "초기 위치 전송: $location")
                } else {
                    Log.e("LocationServiceImpl", "초기 위치 정보 없음")
                }
                location?.let { trySend(it) }
            }

            // 위치 업데이트 요청
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Log.d("LocationServiceImpl", "위치 업데이트 요청 완료")

            // 스트림이 닫히면 위치 업데이트 제거
            awaitClose {
                Log.d("LocationServiceImpl", "위치 업데이트 종료")
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } catch (e: SecurityException) {
            Log.e("LocationServiceImpl", "보안 예외 발생: ${e.message}")
            close(e)
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
