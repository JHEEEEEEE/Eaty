package com.effort.presentation.model.home

import com.effort.domain.model.home.SortType

enum class SortTypeModel {
    DISTANCE,
}

// Mapper 추가
fun SortTypeModel.toDomain(): SortType {
    return when (this) {
        SortTypeModel.DISTANCE -> SortType.DISTANCE
    }
}

fun SortType.toPresentation(): SortTypeModel {
    return when (this) {
        SortType.DISTANCE -> SortTypeModel.DISTANCE
    }
}
