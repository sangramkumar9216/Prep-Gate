package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val status: TopicStatus = TopicStatus.PENDING,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TopicStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
