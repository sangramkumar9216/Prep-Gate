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
    // --- THIS ENTIRE FUNCTION IS REWRITTEN ---
    fun getAllSubjects(): Flow<List<Subject>> {
        // 1. Get a flow of all subjects and a flow of all topics.
        val subjectsFlow = subjectDao.getAllSubjects()
        val topicsFlow = topicDao.getAllTopics()

        // 2. Use `combine` to merge these two flows. The code inside the combine block
        //    will automatically re-run whenever either the subjects list OR the topics list changes.
        return combine(subjectsFlow, topicsFlow) { subjects, topics ->
            subjects.map { subjectEntity ->
                // 3. For each subject, calculate its progress based on the latest list of topics.
                val relevantTopics = topics.filter { it.subjectId == subjectEntity.id }
                val totalTopics = relevantTopics.size
                val completedTopics = relevantTopics.count { it.status == TopicStatus.COMPLETED }
                val progress = if (totalTopics > 0) completedTopics.toFloat() / totalTopics else 0f

                Subject(
                    id = subjectEntity.id,
                    name = subjectEntity.name,
                    color = subjectEntity.color,
                    progress = progress,
                    totalTopics = totalTopics,
                    completedTopics = completedTopics
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