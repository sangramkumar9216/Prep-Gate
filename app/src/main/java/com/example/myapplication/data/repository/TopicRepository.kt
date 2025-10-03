package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.TopicDao
import com.example.myapplication.data.entity.TopicEntity
import com.example.myapplication.data.entity.TopicStatus
import com.example.myapplication.domain.model.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepository @Inject constructor(
    private val topicDao: TopicDao
) {
    fun getTopicsBySubjectId(subjectId: Long): Flow<List<Topic>> {
        return topicDao.getTopicsBySubjectId(subjectId).map { entities ->
            entities.map { entity ->
                Topic(
                    id = entity.id,
                    subjectId = entity.subjectId,
                    title = entity.title,
                    status = entity.status,
                    notes = entity.notes,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    suspend fun getTopicById(id: Long): Topic? {
        val entity = topicDao.getTopicById(id) ?: return null
        return Topic(
            id = entity.id,
            subjectId = entity.subjectId,
            title = entity.title,
            status = entity.status,
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }

    suspend fun insertTopic(topic: TopicEntity): Long {
        return topicDao.insertTopic(topic)
    }

    suspend fun insertTopics(topics: List<TopicEntity>) {
        topicDao.insertTopics(topics)
    }

    suspend fun updateTopic(topic: TopicEntity) {
        topicDao.updateTopic(topic)
    }

    suspend fun deleteTopic(topic: TopicEntity) {
        topicDao.deleteTopic(topic)
    }

    suspend fun deleteTopicsBySubjectId(subjectId: Long) {
        topicDao.deleteTopicsBySubjectId(subjectId)
    }

    suspend fun deleteAllTopics() {
        topicDao.deleteAllTopics()
    }
}
