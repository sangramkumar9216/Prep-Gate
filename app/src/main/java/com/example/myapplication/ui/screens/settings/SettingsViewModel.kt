package com.example.myapplication.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.preferences.PreferencesManager
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.data.repository.TodoRepository
import com.example.myapplication.data.repository.GoalRepository
import com.example.myapplication.data.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val subjectRepository: SubjectRepository,
    private val todoRepository: TodoRepository,
    private val goalRepository: GoalRepository,
    private val topicRepository: TopicRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val isDarkTheme = preferencesManager.isDarkTheme
    val gateExamDate = preferencesManager.gateExamDate
    val pomodoroStudyDuration = preferencesManager.pomodoroStudyDuration
    val pomodoroBreakDuration = preferencesManager.pomodoroBreakDuration

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = isDarkTheme.value
            preferencesManager.setDarkTheme(!currentTheme)
        }
    }

    fun updateGateExamDate(dateMillis: Long) {
        viewModelScope.launch {
            preferencesManager.setGateExamDate(dateMillis)
        }
    }

    fun updatePomodoroDurations(studyMinutes: Long, breakMinutes: Long) {
        viewModelScope.launch {
            preferencesManager.setPomodoroDurations(studyMinutes, breakMinutes)
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val exportData = JSONObject()
                
                // Export subjects
                val subjects = subjectRepository.getAllSubjects().first()
                
                val subjectsArray = JSONArray()
                subjects.forEach { subject ->
                    val subjectJson = JSONObject()
                    subjectJson.put("id", subject.id)
                    subjectJson.put("name", subject.name)
                    subjectJson.put("color", subject.color)
                    subjectJson.put("progress", subject.progress)
                    subjectJson.put("totalTopics", subject.totalTopics)
                    subjectJson.put("completedTopics", subject.completedTopics)
                    subjectsArray.put(subjectJson)
                }
                exportData.put("subjects", subjectsArray)

                // Export todos
                val todos = todoRepository.getAllTodos().first()
                
                val todosArray = JSONArray()
                todos.forEach { todo ->
                    val todoJson = JSONObject()
                    todoJson.put("id", todo.id)
                    todoJson.put("title", todo.title)
                    todoJson.put("description", todo.description)
                    todoJson.put("subjectId", todo.subjectId)
                    todoJson.put("priority", todo.priority.name)
                    todoJson.put("isDone", todo.isDone)
                    todoJson.put("createdAt", todo.createdAt)
                    todoJson.put("dueDate", todo.dueDate)
                    todosArray.put(todoJson)
                }
                exportData.put("todos", todosArray)

                // Export goals
                val goals = goalRepository.getAllGoals().first()
                
                val goalsArray = JSONArray()
                goals.forEach { goal ->
                    val goalJson = JSONObject()
                    goalJson.put("id", goal.id)
                    goalJson.put("title", goal.title)
                    goalJson.put("isDoneForDate", goal.isDoneForDate)
                    goalJson.put("createdAt", goal.createdAt)
                    goalsArray.put(goalJson)
                }
                exportData.put("goals", goalsArray)

                // Add export metadata
                exportData.put("exportDate", System.currentTimeMillis())
                exportData.put("appVersion", "1.0")

                // Save to file
                val fileName = "gate_prep_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
                val file = File(context.getExternalFilesDir(null), fileName)
                file.writeText(exportData.toString())

                // TODO: Show success message or trigger share intent
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            subjectRepository.deleteAllSubjects()
            todoRepository.deleteAllTodos()
            goalRepository.deleteAllGoals()
            topicRepository.deleteAllTopics()
        }
    }
}
