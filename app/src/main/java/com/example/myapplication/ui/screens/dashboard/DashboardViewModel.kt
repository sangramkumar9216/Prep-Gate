package com.example.myapplication.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.preferences.PreferencesManager
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.data.repository.TodoRepository
import com.example.myapplication.data.repository.GoalRepository
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.model.Goal
import com.example.myapplication.notification.PomodoroNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val isDarkTheme = preferencesManager.isDarkTheme
    val gateExamDate = preferencesManager.gateExamDate
    val pomodoroStudyDuration = preferencesManager.pomodoroStudyDuration
    val pomodoroBreakDuration = preferencesManager.pomodoroBreakDuration

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingTodos: StateFlow<List<Todo>> = todoRepository.getPendingTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayGoals: StateFlow<List<Goal>> = goalRepository.getGoalsForDate(getTodayDateString())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var pomodoroTimer: Timer? = null

    fun startPomodoro() {
        if (_pomodoroState.value == PomodoroState.IDLE) {
            _pomodoroState.value = PomodoroState.RUNNING
            startTimer()
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
        _pomodoroState.value = PomodoroState.IDLE
        stopTimer()
        _timeRemaining.value = 0L
        _isStudySession.value = true
    }

    private fun startTimer() {
        pomodoroTimer = Timer()
        pomodoroTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (_timeRemaining.value <= 0) {
                    // Session completed
                    viewModelScope.launch {
                        completeSession()
                    }
                } else {
                    _timeRemaining.value -= 1000
                }
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        pomodoroTimer?.cancel()
        pomodoroTimer = null
    }

    private suspend fun completeSession() {
        stopTimer()
        _pomodoroState.value = PomodoroState.COMPLETED
        
        // Toggle between study and break sessions
        _isStudySession.value = !_isStudySession.value
        
        // Set time for next session
        val duration = if (_isStudySession.value) {
            pomodoroStudyDuration.value * 60 * 1000 // Convert minutes to milliseconds
        } else {
            pomodoroBreakDuration.value * 60 * 1000
        }
        _timeRemaining.value = duration
        
        // Show notification
        if (_isStudySession.value) {
            notificationManager.showBreakSessionEndNotification()
        } else {
            notificationManager.showStudySessionEndNotification()
        }
    }

    fun initializePomodoro() {
        viewModelScope.launch {
            val duration = pomodoroStudyDuration.value * 60 * 1000
            _timeRemaining.value = duration
            _isStudySession.value = true
        }
    }

    private fun getTodayDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
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
