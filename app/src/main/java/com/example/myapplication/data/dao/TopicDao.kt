package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.entity.TopicEntity
import com.example.myapplication.data.entity.TopicStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    // It returns a flow of all topics, which will emit a new list whenever any topic changes.
    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY title ASC")
    fun getTopicsBySubjectId(subjectId: Long): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: Long): TopicEntity?

    @Query("SELECT COUNT(*) FROM topics WHERE subjectId = :subjectId")
    suspend fun getTopicCountBySubjectId(subjectId: Long): Int

    @Query("SELECT COUNT(*) FROM topics WHERE subjectId = :subjectId AND status = :status")
    suspend fun getTopicCountBySubjectIdAndStatus(subjectId: Long, status: TopicStatus): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    @Query("DELETE FROM topics WHERE subjectId = :subjectId")
    suspend fun deleteTopicsBySubjectId(subjectId: Long)

    @Query("DELETE FROM topics")
    suspend fun deleteAllTopics()
}