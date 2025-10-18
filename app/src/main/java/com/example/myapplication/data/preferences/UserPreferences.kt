package com.example.myapplication.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferences {
    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    val GATE_EXAM_DATE = longPreferencesKey("gate_exam_date")
    val GATE_EXAM_TITLE = stringPreferencesKey("gate_exam_title")
    val POMODORO_STUDY_DURATION = longPreferencesKey("pomodoro_study_duration")
    val POMODORO_BREAK_DURATION = longPreferencesKey("pomodoro_break_duration")
    val STUDY_STREAK_COUNT = intPreferencesKey("study_streak_count")
    val LAST_STUDY_SESSION_DATE = stringPreferencesKey("last_study_session_date")
}