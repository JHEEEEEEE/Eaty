package com.effort.presentation.model.home.restaurant.navigation

import com.effort.domain.model.home.restaurant.navigation.NavigationPath

data class NavigationPathModel(
    val latitude: Double, val longitude: Double
) {
    fun toDomain(): NavigationPath {
        return NavigationPath(latitude, longitude)
    }
}

fun NavigationPath.toPresentation(): NavigationPathModel {
    return NavigationPathModel(latitude, longitude)
}