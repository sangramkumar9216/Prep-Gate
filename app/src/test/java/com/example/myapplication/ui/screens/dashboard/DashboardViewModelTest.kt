package com.example.myapplication.ui.screens.dashboard

import com.example.myapplication.MainCoroutineRule
import com.example.myapplication.data.preferences.PreferencesManager
import com.example.myapplication.data.repository.GoalRepository
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.data.repository.TodoRepository
import com.example.myapplication.notification.PomodoroNotificationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTest {

    // This rule swaps the Main dispatcher for a test dispatcher
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Mock all dependencies
    @Mock
    private lateinit var preferencesManager: PreferencesManager
    @Mock
    private lateinit var subjectRepository: SubjectRepository
    @Mock
    private lateinit var todoRepository: TodoRepository
    @Mock
    private lateinit var goalRepository: GoalRepository
    @Mock
    private lateinit var notificationManager: PomodoroNotificationManager

    // The class we are testing
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        // Mock the default preference values before creating the ViewModel
        whenever(preferencesManager.pomodoroStudyDuration).thenReturn(flowOf(25L))
        whenever(preferencesManager.pomodoroBreakDuration).thenReturn(flowOf(5L))
        whenever(preferencesManager.gateExamDate).thenReturn(flowOf(0L))
        whenever(preferencesManager.gateExamTitle).thenReturn(flowOf(""))

        // Mock the repository flows to return empty lists
        whenever(subjectRepository.getAllSubjects()).thenReturn(flowOf(emptyList()))
        whenever(todoRepository.getPendingTodos()).thenReturn(flowOf(emptyList()))
        whenever(goalRepository.getGoalsForDate(any()))
            .thenReturn(flowOf(emptyList()))

        // Initialize the ViewModel
        viewModel = DashboardViewModel(
            preferencesManager,
            subjectRepository,
            todoRepository,
            goalRepository,
            notificationManager
        )
    }

    @Test
    fun `test initial pomodoro state is IDLE`() = runTest {
        // When the ViewModel is initialized
        // Then the state should be IDLE
        assertEquals(PomodoroState.IDLE, viewModel.pomodoroState.value)
    }

    @Test
    fun `test startPomodoro changes state to RUNNING`() = runTest {
        // Given the initial state is IDLE
        assertEquals(PomodoroState.IDLE, viewModel.pomodoroState.value)

        // When startPomodoro is called
        viewModel.startPomodoro()

        // Then the state should be RUNNING
        assertEquals(PomodoroState.RUNNING, viewModel.pomodoroState.value)
    }

    @Test
    fun `test pausePomodoro changes state to PAUSED`() = runTest {
        // Given the timer is running
        viewModel.startPomodoro()
        assertEquals(PomodoroState.RUNNING, viewModel.pomodoroState.value)

        // When pausePomodoro is called
        viewModel.pausePomodoro()

        // Then the state should be PAUSED
        assertEquals(PomodoroState.PAUSED, viewModel.pomodoroState.value)
    }

    @Test
    fun `test resumePomodoro changes state to RUNNING`() = runTest {
        // Given the timer is paused
        viewModel.startPomodoro()
        viewModel.pausePomodoro()
        assertEquals(PomodoroState.PAUSED, viewModel.pomodoroState.value)

        // When resumePomodoro is called
        viewModel.resumePomodoro()

        // Then the state should be RUNNING
        assertEquals(PomodoroState.RUNNING, viewModel.pomodoroState.value)
    }

    @Test
    fun `test resetPomodoro changes state to IDLE`() = runTest {
        // Given the timer is running
        viewModel.startPomodoro()
        assertEquals(PomodoroState.RUNNING, viewModel.pomodoroState.value)

        // When resetPomodoro is called
        viewModel.resetPomodoro()

        // Then the state should be IDLE
        assertEquals(PomodoroState.IDLE, viewModel.pomodoroState.value)
    }

    @Test
    fun `test timer completion switches to break session`() = runTest {
        // Given a study session is running
        viewModel.initializePomodoro()
        viewModel.startPomodoro()
        assertEquals(true, viewModel.isStudySession.value)

        // When the timer completes (by advancing time)
        mainCoroutineRule.testDispatcher.scheduler
            .advanceTimeBy(25 * 60 * 1000 + 100) // 25 minutes + buffer

        // Then the state should be COMPLETED
        assertEquals(PomodoroState.COMPLETED, viewModel.pomodoroState.value)

        // And it should now be a break session
        assertEquals(false, viewModel.isStudySession.value)

        // And the remaining time should be set for the break
        assertEquals(5 * 60 * 1000, viewModel.timeRemaining.value)
    }
}