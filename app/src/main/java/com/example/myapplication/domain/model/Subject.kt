package com.example.myapplication.domain.model

data class Subject(
    val id: Long,
    val name: String,
    val color: String?,
    val progress: Float = 0f, // 0.0 to 1.0
    val totalTopics: Int = 0,
    val completedTopics: Int = 0
)
