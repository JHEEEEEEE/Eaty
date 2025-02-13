package com.effort.remote.model.home.restaurant.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
    네이버 Directions API 응답 구조

    {
        "result": [
            {
                "route": {
                    "trafast": [
                        {
                            "summary": {
                                "distance": 10239,
                                "duration": 15783
                            },
                            "path": [
                                [127.1025, 37.5060],
                                [127.1035, 37.5070],
                                [127.1045, 37.5080]
                            ]
                        }
                    ]
                }
            }
        ]
    }

    📌 구조 설명
    - NavigationPathWrapperResponse: 네이버 API의 최상위 응답 (`result` 리스트 포함)
    - NavigationPathResponse: `result` 리스트 안의 개별 응답 (각각 하나의 경로 정보)
    - RouteResponse: `route` 데이터 (경로 관련 정보 포함)
    - TraFastResponse: `trafast` 데이터 (가장 빠른 경로)
    - `path`: `[longitude, latitude]` 형식의 경로 좌표 리스트
*/

/* 네이버 API에서 반환하는 개별 경로 응답 (result 리스트 안의 요소) */
@Serializable
data class NavigationPathResponse(
    @SerialName("route")
    val route: RouteResponse // 경로 관련 정보
)