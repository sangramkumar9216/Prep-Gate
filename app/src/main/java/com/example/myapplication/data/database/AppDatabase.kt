package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.myapplication.data.dao.*
import com.example.myapplication.data.entity.*

@Database(
    entities = [
        SubjectEntity::class,
        TopicEntity::class,
        TodoEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun todoDao(): TodoDao
    abstract fun goalDao(): GoalDao

    companion object {
        const val DATABASE_NAME = "gate_prep_database"
    }
}
