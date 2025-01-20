package com.effort.presentation.model.map

data class FilterModel (
    val id: Int,               // 필터 ID (고유 식별자)
    val displayName: String,   // 화면에 표시할 이름
    val query: String,         // 서버 요청 시 사용할 쿼리 값
    var isSelected: Boolean = false // 선택 여부 (UI 관리)
)