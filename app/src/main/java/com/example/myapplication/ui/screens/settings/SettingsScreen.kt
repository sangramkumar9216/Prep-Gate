package com.example.myapplication.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val gateExamDate by viewModel.gateExamDate.collectAsState()
    val pomodoroStudyDuration by viewModel.pomodoroStudyDuration.collectAsState()
    val pomodoroBreakDuration by viewModel.pomodoroBreakDuration.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showPomodoroSettings by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Theme Settings
            SettingsCard(
                title = "Appearance",
                icon = Icons.Default.ColorLens
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Theme",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
            }
        }

        item {
            // GATE Exam Date
            SettingsCard(
                title = "GATE Exam",
                icon = Icons.Default.Schedule
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exam Date",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(gateExamDate))
                            )
                        }
                    }
                }
            }
        }

        item {
            // Pomodoro Settings
            SettingsCard(
                title = "Pomodoro Timer",
                icon = Icons.Default.Timer
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Duration: ${pomodoroStudyDuration} min",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Break Duration: ${pomodoroBreakDuration} min",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(onClick = { showPomodoroSettings = true }) {
                        Text("Customize")
                    }
                }
            }
        }

        item {
            // Data Management
            SettingsCard(
                title = "Data Management",
                icon = Icons.Default.Folder
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Export Data",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = {
                                // Launch coroutine to handle async export
                                // Note: In a real app, you'd want to show loading state
                                viewModel.exportData()
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Export")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reset All Data",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        IconButton(
                            onClick = { showResetDialog = true }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Reset",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        item {
            // App Info
            SettingsCard(
                title = "App Information",
                icon = Icons.Default.Info
            ) {
                Column {
                    Text(
                        text = "GATE CSE 2026 Prep",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Version 1.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Built with Kotlin & Jetpack Compose",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = gateExamDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { dateMillis ->
                viewModel.updateGateExamDate(dateMillis)
                showDatePicker = false
            }
        )
    }

    // Pomodoro Settings Dialog
    if (showPomodoroSettings) {
        PomodoroSettingsDialog(
            studyDuration = pomodoroStudyDuration,
            breakDuration = pomodoroBreakDuration,
            onDismiss = { showPomodoroSettings = false },
            onConfirm = { study, breakDuration ->
                viewModel.updatePomodoroDurations(study, breakDuration)
                showPomodoroSettings = false
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Data") },
            text = { 
                Text("Are you sure you want to delete all your data? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
fun DatePickerDialog(
    initialDate: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select GATE Exam Date") },
        text = {
            Column {
                Text(
                    text = "Choose the date for your GATE CSE 2026 exam",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Simple date input for now - in a real app, you'd use a proper date picker
                OutlinedTextField(
                    value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(selectedDate)),
                    onValueChange = { /* Handle date input */ },
                    label = { Text("Date (YYYY-MM-DD)") },
                    readOnly = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(selectedDate) }
            ) {
                Text("Set Date")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PomodoroSettingsDialog(
    studyDuration: Long,
    breakDuration: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit
) {
    var studyMinutes by remember { mutableStateOf(studyDuration.toString()) }
    var breakMinutes by remember { mutableStateOf(breakDuration.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pomodoro Settings") },
        text = {
            Column {
                OutlinedTextField(
                    value = studyMinutes,
                    onValueChange = { studyMinutes = it },
                    label = { Text("Study Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = breakMinutes,
                    onValueChange = { breakMinutes = it },
                    label = { Text("Break Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val study = studyMinutes.toLongOrNull() ?: 25L
                    val breakDur = breakMinutes.toLongOrNull() ?: 5L
                    onConfirm(study, breakDur)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
