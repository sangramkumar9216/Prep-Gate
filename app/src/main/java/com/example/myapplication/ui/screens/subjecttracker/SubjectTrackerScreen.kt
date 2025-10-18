package com.example.myapplication.ui.screens.subjecttracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.entity.TopicStatus
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Topic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectTrackerScreen(
    viewModel: SubjectTrackerViewModel = hiltViewModel(),
    onMenuClick: () -> Unit // Accept the click handler
) {
    val subjects by viewModel.subjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val topics by viewModel.topics.collectAsState()

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showTopicDetailsDialog by remember { mutableStateOf(false) }
    var selectedTopic by remember { mutableStateOf<Topic?>(null) }

    val currentSubject = selectedSubject

    Scaffold(
        floatingActionButton = {
            // FAB changes its function based on the current view
            FloatingActionButton(
                onClick = {
                    if (currentSubject == null) {
                        showAddSubjectDialog = true
                    } else {
                        showAddTopicDialog = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (currentSubject == null) "Add Subject" else "Add Topic"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // --- NEW DYNAMIC TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title changes based on the view
                Text(
                    text = currentSubject?.name ?: "Subjects",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                // Icon button changes based on the view
                IconButton(
                    onClick = {
                        if (currentSubject == null) {
                            onMenuClick()
                        } else {
                            viewModel.selectSubject(null)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentSubject == null) Icons.Default.Menu else Icons.Default.ArrowBack,
                        contentDescription = if (currentSubject == null) "Open Drawer" else "Back"
                    )
                }
            }

            if (currentSubject == null) {
                SubjectListView(
                    subjects = subjects,
                    onSubjectSelected = { subject -> viewModel.selectSubject(subject) },
                    onDeleteSubject = { subject -> viewModel.deleteSubject(subject) }
                )
            } else {
                TopicListView(
                    subject = currentSubject,
                    topics = topics,
                    onTopicClick = { topic ->
                        selectedTopic = topic
                        showTopicDetailsDialog = true
                    },
                    onTopicStatusChange = { topic, newStatus -> viewModel.updateTopicStatus(topic, newStatus) },
                    onDeleteTopic = { topic -> viewModel.deleteTopic(topic) }
                )
            }
        }
    }

    // --- DIALOGS (UNCHANGED) ---
    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name ->
                viewModel.addSubject(name)
                showAddSubjectDialog = false
            }
        )
    }

    if (showAddTopicDialog) {
        AddTopicDialog(
            onDismiss = { showAddTopicDialog = false },
            onConfirm = { title ->
                selectedSubject?.let { subject ->
                    viewModel.addTopic(subject.id, title)
                }
                showAddTopicDialog = false
            }
        )
    }

    if (showTopicDetailsDialog && selectedTopic != null) {
        TopicDetailsDialog(
            topic = selectedTopic!!,
            onDismiss = {
                showTopicDetailsDialog = false
                selectedTopic = null
            },
            onUpdateNotes = { topic, notes -> viewModel.updateTopicNotes(topic, notes) },
        )
    }
}

// --- UPDATED COMPOSABLES ---

@Composable
fun TopicListView(
    subject: Subject,
    topics: List<Topic>,
    onTopicClick: (Topic) -> Unit,
    onTopicStatusChange: (Topic, TopicStatus) -> Unit,
    onDeleteTopic: (Topic) -> Unit
) {
    Column {
        // Progress Bar and Summary
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            LinearProgressIndicator(
                progress = { subject.progress },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${subject.completedTopics} of ${subject.totalTopics} topics",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(subject.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Status Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Topic Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    StatusChip("Pending", topics.count { it.status == TopicStatus.PENDING }, Color.Gray)
                    StatusChip("In Progress", topics.count { it.status == TopicStatus.IN_PROGRESS }, Color.Blue)
                    StatusChip("Completed", topics.count { it.status == TopicStatus.COMPLETED }, Color.Green)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Empty state or LazyColumn
        if (topics.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No topics yet. Tap the '+' button to add one!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(topics) { topic ->
                    TopicCard(
                        topic = topic,
                        onClick = { onTopicClick(topic) },
                        onStatusChange = { newStatus -> onTopicStatusChange(topic, newStatus) },
                        onDelete = { onDeleteTopic(topic) }
                    )
                }
            }
        }
    }
}


@Composable
fun TopicDetailsDialog(
    topic: Topic,
    onDismiss: () -> Unit,
    onUpdateNotes: (Topic, String) -> Unit
) {
    var notes by remember { mutableStateOf(topic.notes ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(topic.title) },
        text = {
            Column {
                Text(
                    text = "Status: ${topic.status.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdateNotes(topic, notes)
                    onDismiss()
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// All other composables (SubjectListView, SubjectCard, TopicCard, etc.) remain the same as the previous version.
// I have included them here for completeness.

@Composable
fun SubjectListView(
    subjects: List<Subject>,
    onSubjectSelected: (Subject) -> Unit,
    onDeleteSubject: (Subject) -> Unit
) {
    if (subjects.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No subjects yet. Tap the '+' button to add one!")
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = { onSubjectSelected(subject) },
                    onDelete = { onDeleteSubject(subject) }
                )
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${(subject.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { subject.progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${subject.completedTopics}/${subject.totalTopics} topics completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Subject") },
            text = { Text("Are you sure you want to delete '${subject.name}' and all its topics? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun StatusChip(
    label: String,
    count: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: $count",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun TopicCard(
    topic: Topic,
    onClick: () -> Unit,
    onStatusChange: (TopicStatus) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = topic.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            StatusDropdown(currentStatus = topic.status, onStatusChange = onStatusChange)
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Topic", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Topic") },
            text = { Text("Are you sure you want to delete '${topic.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatusDropdown(
    currentStatus: TopicStatus,
    onStatusChange: (TopicStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(
                text = currentStatus.name.replace("_", " "),
                style = MaterialTheme.typography.bodySmall
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TopicStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = status.name.replace("_", " "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Subject") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Subject Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
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
fun AddTopicDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Topic") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Topic Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title) },
                enabled = title.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}