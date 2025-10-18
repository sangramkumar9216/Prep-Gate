package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    // This StateFlow will automatically update whenever the isDarkTheme preference changes.
    val isDarkTheme: StateFlow<Boolean> = preferencesManager.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            // Start collecting immediately and share the value.
            started = SharingStarted.WhileSubscribed(5000),
            // The initial value before the preference is loaded.
            initialValue = false
        )
}