package com.example.myapplication.domain.model

import com.example.myapplication.data.entity.TodoPriority

data class Todo(
    val id: Long,
    val title: String,
    val description: String?,
    val subjectId: Long?,
    val subjectName: String? = null,
    val priority: TodoPriority,
    val isDone: Boolean,
    val createdAt: Long,
    val dueDate: Long?
)
