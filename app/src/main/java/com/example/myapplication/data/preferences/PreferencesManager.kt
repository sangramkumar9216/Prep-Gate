package com.example.myapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[UserPreferences.IS_DARK_THEME] ?: false
    }

    val gateExamDate: Flow<Long> = dataStore.data.map { preferences ->
        preferences[UserPreferences.GATE_EXAM_DATE] ?: getDefaultGateDate()
    }

    val pomodoroStudyDuration: Flow<Long> = dataStore.data.map { preferences ->
        preferences[UserPreferences.POMODORO_STUDY_DURATION] ?: 25L // 25 minutes
    }

    val pomodoroBreakDuration: Flow<Long> = dataStore.data.map { preferences ->
        preferences[UserPreferences.POMODORO_BREAK_DURATION] ?: 5L // 5 minutes
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

    suspend fun setPomodoroDurations(studyMinutes: Long, breakMinutes: Long) {
        dataStore.edit { preferences ->
            preferences[UserPreferences.POMODORO_STUDY_DURATION] = studyMinutes
            preferences[UserPreferences.POMODORO_BREAK_DURATION] = breakMinutes
        }
    }

    private fun getDefaultGateDate(): Long {
        // Default to February 1, 2026
        val calendar = java.util.Calendar.getInstance()
        calendar.set(2026, 1, 1, 0, 0, 0) // Month is 0-based, so 1 = February
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
