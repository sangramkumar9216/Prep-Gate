package com.example.myapplication.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferences {
    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    val GATE_EXAM_DATE = longPreferencesKey("gate_exam_date")
    val POMODORO_STUDY_DURATION = longPreferencesKey("pomodoro_study_duration") // in minutes
    val POMODORO_BREAK_DURATION = longPreferencesKey("pomodoro_break_duration") // in minutes
}
