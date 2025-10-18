package com.example.myapplication.ui.screens.subjecttracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.SubjectEntity
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
import kotlin.random.Random

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

    fun selectSubject(subject: Subject?) {
        _selectedSubject.value = subject
        if (subject != null) {
            loadTopicsForSubject(subject.id)
        } else {
            _topics.value = emptyList()
        }
    }

    private fun loadTopicsForSubject(subjectId: Long) {
        viewModelScope.launch {
            topicRepository.getTopicsBySubjectId(subjectId).collect { topicsList ->
                _topics.value = topicsList
            }
        }
    }

    // --- NEW FUNCTION: Add a subject ---
    fun addSubject(name: String) {
        viewModelScope.launch {
            val newSubject = SubjectEntity(
                name = name,
                color = getRandomHexColor()
            )
            subjectRepository.insertSubject(newSubject)
        }
    }

    // --- NEW FUNCTION: Delete a subject ---
    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            // Recreate the entity from the domain model to pass to the repository
            val subjectEntity = SubjectEntity(
                id = subject.id,
                name = subject.name,
                color = subject.color
            )
            subjectRepository.deleteSubject(subjectEntity)
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

    // Helper to assign a random color to new subjects
    private fun getRandomHexColor(): String {
        val random = Random.Default
        val color = android.graphics.Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        return String.format("#%06X", 0xFFFFFF and color)
    }
}