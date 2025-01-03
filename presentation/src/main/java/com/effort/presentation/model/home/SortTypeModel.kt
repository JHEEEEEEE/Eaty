package com.effort.presentation.model.home

import com.effort.domain.model.home.SortType

enum class SortTypeModel {
    DEFAULT,
    NAME
}

// Mapper 추가
fun SortTypeModel.toDomain(): SortType {
    return when (this) {
        SortTypeModel.DEFAULT -> SortType.DEFAULT
        SortTypeModel.NAME -> SortType.NAME
    }
}

fun SortType.toPresentation(): SortTypeModel {
    return when (this) {
        SortType.DEFAULT -> SortTypeModel.DEFAULT
        SortType.NAME -> SortTypeModel.NAME
    }
}
