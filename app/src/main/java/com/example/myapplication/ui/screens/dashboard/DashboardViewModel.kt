package com.example.myapplication.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.preferences.PreferencesManager
import com.example.myapplication.data.repository.GoalRepository
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.data.repository.TodoRepository
import com.example.myapplication.domain.model.Goal
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Todo
import com.example.myapplication.notification.PomodoroNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val subjectRepository: SubjectRepository,
    private val todoRepository: TodoRepository,
    private val goalRepository: GoalRepository,
    private val notificationManager: PomodoroNotificationManager
) : ViewModel() {

    private val _pomodoroState = MutableStateFlow(PomodoroState.IDLE)
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private val _isStudySession = MutableStateFlow(true)
    val isStudySession: StateFlow<Boolean> = _isStudySession.asStateFlow()

    val gateExamDate = preferencesManager.gateExamDate
    val gateExamTitle = preferencesManager.gateExamTitle
    val pomodoroStudyDuration = preferencesManager.pomodoroStudyDuration
    val pomodoroBreakDuration = preferencesManager.pomodoroBreakDuration

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTodos: StateFlow<List<Todo>> = todoRepository.getPendingTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayGoals: StateFlow<List<Goal>> = goalRepository.getGoalsForDate(getTodayDateString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: Job? = null

    fun initializePomodoro() {
        viewModelScope.launch {
            val duration = pomodoroStudyDuration.first() * 60 * 1000
            if (_pomodoroState.value == PomodoroState.IDLE) {
                _timeRemaining.value = duration
            }
        }
    }

    fun startPomodoro() {
        if (_pomodoroState.value == PomodoroState.IDLE || _pomodoroState.value == PomodoroState.COMPLETED) {
            viewModelScope.launch {
                val duration = if (_isStudySession.value) pomodoroStudyDuration.first() else pomodoroBreakDuration.first()
                _timeRemaining.value = duration * 60 * 1000
                _pomodoroState.value = PomodoroState.RUNNING
                startTimer()
            }
        }
    }

    fun pausePomodoro() {
        if (_pomodoroState.value == PomodoroState.RUNNING) {
            _pomodoroState.value = PomodoroState.PAUSED
            stopTimer()
        }
    }

    fun resumePomodoro() {
        if (_pomodoroState.value == PomodoroState.PAUSED) {
            _pomodoroState.value = PomodoroState.RUNNING
            startTimer()
        }
    }

    fun resetPomodoro() {
        stopTimer()
        _pomodoroState.value = PomodoroState.IDLE
        _isStudySession.value = true
        initializePomodoro()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000)
                _timeRemaining.value -= 1000
            }
            _timeRemaining.value = 0
            completeSession()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun completeSession() {
        stopTimer()
        if (_isStudySession.value) {
            notificationManager.showStudySessionEndNotification()
        } else {
            notificationManager.showBreakSessionEndNotification()
        }

        _isStudySession.value = !_isStudySession.value
        _pomodoroState.value = PomodoroState.COMPLETED
        initializePomodoro()
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

enum class PomodoroState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}