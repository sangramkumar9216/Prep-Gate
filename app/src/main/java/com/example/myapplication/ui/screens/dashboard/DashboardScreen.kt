package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.Goal
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val pomodoroState by viewModel.pomodoroState.collectAsState(initial = PomodoroState.IDLE)
    val timeRemaining by viewModel.timeRemaining.collectAsState(initial = 0L)
    val isStudySession by viewModel.isStudySession.collectAsState(initial = true)
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val pendingTodos by viewModel.pendingTodos.collectAsState(initial = emptyList())
    val todayGoals by viewModel.todayGoals.collectAsState(initial = emptyList())
    val gateExamDate by viewModel.gateExamDate.collectAsState(initial = System.currentTimeMillis())
    val gateExamTitle by viewModel.gateExamTitle.collectAsState(initial = "GATE CSE 2026")

    val studyDuration by viewModel.pomodoroStudyDuration.collectAsState(initial = 25L)
    val breakDuration by viewModel.pomodoroBreakDuration.collectAsState(initial = 5L)
    val totalPomodoroTime = if (isStudySession) studyDuration * 60 * 1000 else breakDuration * 60 * 1000

    LaunchedEffect(Unit) {
        viewModel.initializePomodoro()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(onMenuClick = onMenuClick)
        }
        item {
            GateCountdownCard(gateExamTitle = gateExamTitle, gateExamDate = gateExamDate)
        }
        item {
            PomodoroTimerCard(
                pomodoroState = pomodoroState,
                timeRemaining = timeRemaining,
                totalTime = totalPomodoroTime,
                isStudySession = isStudySession,
                onStart = { viewModel.startPomodoro() },
                onPause = { viewModel.pausePomodoro() },
                onResume = { viewModel.resumePomodoro() },
                onReset = { viewModel.resetPomodoro() }
            )
        }

        item {
            SubjectProgressCard(subjects = subjects)
        }
        item {
            RecentTasksCard(todos = pendingTodos.take(5))
        }
        item {
            MotivationalQuoteCard()
        }
    }
}

@Composable
fun Header(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onMenuClick) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Drawer")
        }
    }
}

@Composable
fun GateCountdownCard(gateExamTitle: String, gateExamDate: Long) {
    val currentTime = System.currentTimeMillis()
    val timeDiff = gateExamDate - currentTime

    val days = (timeDiff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val hours = ((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).coerceAtLeast(0)
    val minutes = ((timeDiff % (1000 * 60 * 60)) / (1000 * 60)).coerceAtLeast(0)

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = gateExamTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Time Remaining",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                CountdownItem(
                    label = "Days",
                    value = days.toString(),
                    valueColor = Color.White,
                    labelColor = Color.White.copy(alpha = 0.8f)
                )
                CountdownItem(
                    label = "Hours",
                    value = hours.toString(),
                    valueColor = Color.White,
                    labelColor = Color.White.copy(alpha = 0.8f)
                )
                CountdownItem(
                    label = "Minutes",
                    value = minutes.toString(),
                    valueColor = Color.White,
                    labelColor = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CountdownItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = valueColor)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = labelColor)
    }
}

@Composable
fun PomodoroTimerCard(
    pomodoroState: PomodoroState,
    timeRemaining: Long,
    totalTime: Long,
    isStudySession: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = timeRemaining / (1000 * 60)
    val seconds = (timeRemaining % (1000 * 60)) / 1000
    val timeText = String.format("%02d:%02d", minutes, seconds)

    val cardBrush = if (isStudySession) {
        Brush.verticalGradient(listOf(Color(0xBF72CF), Color(0xFFA5D6A7)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9)))
    }

    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isStudySession) "Study Time" else "Break Time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val buttonWidth = 120.dp
                when (pomodoroState) {
                    PomodoroState.IDLE, PomodoroState.COMPLETED -> {
                        Button(onClick = onStart, modifier = Modifier.width(buttonWidth)) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Start")
                        }
                    }
                    PomodoroState.RUNNING -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = onPause, modifier = Modifier.width(buttonWidth)) {
                                Icon(Icons.Default.Pause, contentDescription = "Pause")
                            }
                            OutlinedButton(onClick = onReset) {
                                Icon(Icons.Default.Stop, contentDescription = "Reset")
                            }
                        }
                    }
                    PomodoroState.PAUSED -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = onResume, modifier = Modifier.width(buttonWidth)) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Resume")

                            }
                            OutlinedButton(onClick = onReset) {
                                Icon(Icons.Default.Stop, contentDescription = "Reset")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyGoalsCard(goals: List<Goal>) {
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
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today's Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (goals.isEmpty()) {
                Text(
                    text = "No goals set for today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                goals.forEach { goal ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = { /* TODO */ }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectProgressCard(subjects: List<Subject>) {
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
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Subject Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (subjects.isEmpty()) {
                Text(
                    text = "No subjects available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                subjects.forEach { subject ->
                    SubjectProgressItem(subject = subject)
                }
            }
        }
    }
}

@Composable
fun SubjectProgressItem(subject: Subject) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(subject.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { subject.progress },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${subject.completedTopics}/${subject.totalTopics} topics completed",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RecentTasksCard(todos: List<Todo>) {
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
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recent Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (todos.isEmpty()) {
                Text(
                    text = "No pending tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                todos.forEach { todo ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = todo.isDone,
                            onCheckedChange = { /* TODO */ }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = todo.title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (todo.description != null) {
                                Text(
                                    text = todo.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MotivationalQuoteCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.TipsAndUpdates,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Success is the sum of small efforts repeated day in and day out.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Keep going! You've got this! 💪",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}