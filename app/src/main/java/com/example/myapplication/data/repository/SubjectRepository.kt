package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.SubjectDao
import com.example.myapplication.data.dao.TopicDao
import com.example.myapplication.data.entity.SubjectEntity
import com.example.myapplication.data.entity.TopicStatus
import com.example.myapplication.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val subjectDao: SubjectDao,
    private val topicDao: TopicDao
) {
    fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { subjects ->
            subjects.map { subjectEntity ->
                Subject(
                    id = subjectEntity.id,
                    name = subjectEntity.name,
                    color = subjectEntity.color,
                    progress = 0f, // Will be calculated separately
                    totalTopics = 0,
                    completedTopics = 0
                )
            }
        }
    }

    suspend fun getSubjectWithProgress(subjectId: Long): Subject? {
        val subject = subjectDao.getSubjectById(subjectId) ?: return null
        val totalTopics = topicDao.getTopicCountBySubjectId(subjectId)
        val completedTopics = topicDao.getTopicCountBySubjectIdAndStatus(subjectId, TopicStatus.COMPLETED)
        
        val progress = if (totalTopics > 0) completedTopics.toFloat() / totalTopics else 0f
        
        return Subject(
            id = subject.id,
            name = subject.name,
            color = subject.color,
            progress = progress,
            totalTopics = totalTopics,
            completedTopics = completedTopics
        )
    }

    suspend fun insertSubject(subject: SubjectEntity): Long {
        return subjectDao.insertSubject(subject)
    }

    suspend fun insertSubjects(subjects: List<SubjectEntity>) {
        subjectDao.insertSubjects(subjects)
    }

    suspend fun updateSubject(subject: SubjectEntity) {
        subjectDao.updateSubject(subject)
    }

    suspend fun deleteSubject(subject: SubjectEntity) {
        subjectDao.deleteSubject(subject)
    }

    suspend fun deleteAllSubjects() {
        subjectDao.deleteAllSubjects()
    }
}
