package com.example.myapplication.domain.model

data class Goal(
    val id: Long,
    val title: String,
    val isDoneForDate: String,
    val createdAt: Long
)
