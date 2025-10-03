package com.example.myapplication.domain.model

import com.example.myapplication.data.entity.TopicStatus

data class Topic(
    val id: Long,
    val subjectId: Long,
    val title: String,
    val status: TopicStatus,
    val notes: String?,
    val createdAt: Long
)
