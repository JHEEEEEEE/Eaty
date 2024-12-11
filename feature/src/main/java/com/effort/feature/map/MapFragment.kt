package com.effort.feature.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import com.effort.feature.R
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.util.FusedLocationSource


// 네이버 지도 Fragment 클래스
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate), OnMapReadyCallback {

    // 위치 추적을 위한 FusedLocationSource 객체
    private lateinit var locationSource: FusedLocationSource

    // NaverMap 객체. 지도 관련 설정 및 제어를 위한 객체
    private lateinit var naverMap: NaverMap

    // 현재 위치를 표시하기 위한 LocationOverlay 객체
    private lateinit var locationOverlay: LocationOverlay

    companion object {
        // 새로운 Fragment 인스턴스 생성 메서드
        fun newInstance() = com.effort.feature.map.MapFragment()

        // 위치 권한 요청 코드
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

    }

    override fun initView() {

    }



    // Fragment가 뷰를 생성할 때 호출되는 메서드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 레이아웃을 바인딩하여 반환
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 뷰가 생성된 후 호출되는 메서드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        // 지도 프래그먼트를 childFragmentManager를 통해 설정
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as MapFragment?
            ?: MapFragment.newInstance(
                // NaverMapOptions로 지도 초기 설정
                NaverMapOptions()
                    .camera(CameraPosition(LatLng(37.5112, 127.0590), 16.0, 0.0, 90.0)) // 초기 카메라 위치
                    .indoorEnabled(true) // 실내 지도 활성화
                    .locationButtonEnabled(true) // 위치 버튼 활성화
            ).also {
                // 프래그먼트를 childFragmentManager에 추가
                childFragmentManager.beginTransaction()
                    .replace(binding.map.id, it) // 기존 map View를 MapFragment로 교체
                    .commit()
            }

        // 지도 준비가 완료되면 onMapReady 콜백 호출
        mapFragment.getMapAsync(this)
    }

    // NaverMap 객체가 준비되었을 때 호출되는 메서드
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap // 준비된 NaverMap 객체를 저장

        // 위치 추적을 위한 FusedLocationSource 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // NaverMap 객체에 LocationSource 설정
        naverMap.locationSource = locationSource

        // 현재 위치 오버레이 설정
        setOverlay()
    }

    // 권한 요청 결과를 처리하는 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // FusedLocationSource에서 권한 요청 결과 처리
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부된 경우
                naverMap.locationTrackingMode = LocationTrackingMode.None // 위치 추적 모드 비활성화
            }
            return // 결과 처리 종료
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // 부모 메서드 호출
    }


    // 현재 위치 오버레이를 설정하는 메서드
    private fun setOverlay() {
        locationOverlay = naverMap.locationOverlay.apply {
            // 위치 오버레이를 보이도록 설정
            isVisible = true
        }
    }
}