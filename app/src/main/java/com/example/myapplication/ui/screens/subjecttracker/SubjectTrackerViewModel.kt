package com.example.myapplication.ui.screens.subjecttracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.TopicEntity
import com.example.myapplication.data.entity.TopicStatus
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.data.repository.TopicRepository
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Topic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectTrackerViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.getAllSubjects().collect { subjectsList ->
                _subjects.value = subjectsList
            }
        }
    }

    fun selectSubject(subject: Subject) {
        _selectedSubject.value = subject
        loadTopicsForSubject(subject.id)
    }

    private fun loadTopicsForSubject(subjectId: Long) {
        viewModelScope.launch {
            topicRepository.getTopicsBySubjectId(subjectId).collect { topicsList ->
                _topics.value = topicsList
            }
        }
    }

    fun updateTopicStatus(topic: Topic, newStatus: TopicStatus) {
        viewModelScope.launch {
            val updatedTopic = TopicEntity(
                id = topic.id,
                subjectId = topic.subjectId,
                title = topic.title,
                status = newStatus,
                notes = topic.notes,
                createdAt = topic.createdAt
            )
            topicRepository.updateTopic(updatedTopic)
        }
    }

    fun addTopic(subjectId: Long, title: String) {
        viewModelScope.launch {
            val newTopic = TopicEntity(
                subjectId = subjectId,
                title = title,
                status = TopicStatus.PENDING
            )
            topicRepository.insertTopic(newTopic)
        }
    }

    fun updateTopicNotes(topic: Topic, notes: String) {
        viewModelScope.launch {
            val updatedTopic = TopicEntity(
                id = topic.id,
                subjectId = topic.subjectId,
                title = topic.title,
                status = topic.status,
                notes = notes,
                createdAt = topic.createdAt
            )
            topicRepository.updateTopic(updatedTopic)
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            val topicEntity = TopicEntity(
                id = topic.id,
                subjectId = topic.subjectId,
                title = topic.title,
                status = topic.status,
                notes = topic.notes,
                createdAt = topic.createdAt
            )
            topicRepository.deleteTopic(topicEntity)
        }
    }
}
