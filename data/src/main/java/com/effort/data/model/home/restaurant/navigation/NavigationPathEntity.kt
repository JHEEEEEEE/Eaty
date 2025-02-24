package com.effort.data.model.home.restaurant.navigation

import com.effort.domain.model.home.restaurant.navigation.NavigationPath

data class NavigationPathEntity(
    val latitude: Double, val longitude: Double
) {
    fun toDomain(): NavigationPath {
        return NavigationPath(latitude, longitude)
    }
}

fun NavigationPath.toData(): NavigationPathEntity {
    return NavigationPathEntity(latitude, longitude)
}