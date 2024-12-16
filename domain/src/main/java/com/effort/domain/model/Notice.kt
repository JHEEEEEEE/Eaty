package com.effort.domain.model

data class Notice(
    val category: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val timestamp: String,
)