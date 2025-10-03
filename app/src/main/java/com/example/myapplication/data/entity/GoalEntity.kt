package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val isDoneForDate: String, // Format: "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
)
