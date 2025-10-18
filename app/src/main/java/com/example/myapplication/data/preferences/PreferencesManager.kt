package com.example.myapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// THIS IS THE FIX: The dataStore definition needs to be at the top level of the file.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // The class uses the top-level dataStore
    private val dataStore = context.dataStore

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[UserPreferences.IS_DARK_THEME] ?: false
    }

    val gateExamDate: Flow<Long> = dataStore.data.map { preferences ->
        preferences[UserPreferences.GATE_EXAM_DATE] ?: getDefaultGateDate()
    }

    val gateExamTitle: Flow<String> = dataStore.data.map { preferences ->
        preferences[UserPreferences.GATE_EXAM_TITLE] ?: "GATE CSE 2026"
    }

    val pomodoroStudyDuration: Flow<Long> = dataStore.data.map { preferences ->
        preferences[UserPreferences.POMODORO_STUDY_DURATION] ?: 25L
    }

    val pomodoroBreakDuration: Flow<Long> = dataStore.data.map { preferences ->
        preferences[UserPreferences.POMODORO_BREAK_DURATION] ?: 5L
    }

    val studyStreakCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[UserPreferences.STUDY_STREAK_COUNT] ?: 0
    }

    val lastStudySessionDate: Flow<String> = dataStore.data.map { preferences ->
        preferences[UserPreferences.LAST_STUDY_SESSION_DATE] ?: ""
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferences.IS_DARK_THEME] = isDark
        }
    }

    suspend fun setGateExamDate(dateMillis: Long) {
        dataStore.edit { preferences ->
            preferences[UserPreferences.GATE_EXAM_DATE] = dateMillis
        }
    }

    suspend fun setGateExamTitle(title: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferences.GATE_EXAM_TITLE] = title
        }
    }

    suspend fun setPomodoroDurations(studyMinutes: Long, breakMinutes: Long) {
        dataStore.edit { preferences ->
            preferences[UserPreferences.POMODORO_STUDY_DURATION] = studyMinutes
            preferences[UserPreferences.POMODORO_BREAK_DURATION] = breakMinutes
        }
    }

    suspend fun updateStudyStreak() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastSessionDate = lastStudySessionDate.first()

        if (lastSessionDate != today) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val currentStreak = studyStreakCount.first()
            val newStreak = if (lastSessionDate == yesterday) currentStreak + 1 else 1

            dataStore.edit { preferences ->
                preferences[UserPreferences.STUDY_STREAK_COUNT] = newStreak
                preferences[UserPreferences.LAST_STUDY_SESSION_DATE] = today
            }
        }
    }

    private fun getDefaultGateDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(2026, 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}