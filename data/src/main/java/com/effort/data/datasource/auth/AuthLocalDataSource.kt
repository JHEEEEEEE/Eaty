package com.effort.data.datasource.auth

interface AuthLocalDataSource {

    suspend fun clearUsers()
}