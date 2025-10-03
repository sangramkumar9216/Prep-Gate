package com.example.myapplication.ui.screens.subjecttracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
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
    viewModel: SubjectTrackerViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val topics by viewModel.topics.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showTopicDetailsDialog by remember { mutableStateOf(false) }
    var selectedTopic by remember { mutableStateOf<Topic?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Subject Tracker",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (selectedSubject == null) {
            // Subject List View
            SubjectListView(
                subjects = subjects,
                onSubjectSelected = { subject ->
                    viewModel.selectSubject(subject)
                }
            )
        } else {
            // Topic List View
            TopicListView(
                subject = selectedSubject!!,
                topics = topics,
                onBackClick = {
                    viewModel.selectSubject(null)
                },
                onAddTopicClick = {
                    showAddTopicDialog = true
                },
                onTopicClick = { topic ->
                    selectedTopic = topic
                    showTopicDetailsDialog = true
                },
                onTopicStatusChange = { topic, newStatus ->
                    viewModel.updateTopicStatus(topic, newStatus)
                }
            )
        }
    }

    // Add Topic Dialog
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

    // Topic Details Dialog
    if (showTopicDetailsDialog && selectedTopic != null) {
        TopicDetailsDialog(
            topic = selectedTopic!!,
            onDismiss = { 
                showTopicDetailsDialog = false
                selectedTopic = null
            },
            onUpdateNotes = { topic, notes ->
                viewModel.updateTopicNotes(topic, notes)
            },
            onDelete = { topic ->
                viewModel.deleteTopic(topic)
                showTopicDetailsDialog = false
                selectedTopic = null
            }
        )
    }
}

@Composable
fun SubjectListView(
    subjects: List<Subject>,
    onSubjectSelected: (Subject) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(subjects) { subject ->
            SubjectCard(
                subject = subject,
                onClick = { onSubjectSelected(subject) }
            )
        }
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${(subject.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = subject.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${subject.completedTopics}/${subject.totalTopics} topics completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TopicListView(
    subject: Subject,
    topics: List<Topic>,
    onBackClick: () -> Unit,
    onAddTopicClick: () -> Unit,
    onTopicClick: (Topic) -> Unit,
    onTopicStatusChange: (Topic, TopicStatus) -> Unit
) {
    Column {
        // Header with back button and add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onAddTopicClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Topic")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress summary
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Progress Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusChip("Pending", topics.count { it.status == TopicStatus.PENDING }, Color.Gray)
                    StatusChip("In Progress", topics.count { it.status == TopicStatus.IN_PROGRESS }, Color.Orange)
                    StatusChip("Completed", topics.count { it.status == TopicStatus.COMPLETED }, Color.Green)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Topics list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topics) { topic ->
                TopicCard(
                    topic = topic,
                    onClick = { onTopicClick(topic) },
                    onStatusChange = { newStatus ->
                        onTopicStatusChange(topic, newStatus)
                    }
                )
            }
        }
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
    onStatusChange: (TopicStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                StatusDropdown(
                    currentStatus = topic.status,
                    onStatusChange = onStatusChange
                )
            }
            
            if (topic.notes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = topic.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusDropdown(
    currentStatus: TopicStatus,
    onStatusChange: (TopicStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        OutlinedButton(
            onClick = { expanded = true }
        ) {
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
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
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

@Composable
fun TopicDetailsDialog(
    topic: Topic,
    onDismiss: () -> Unit,
    onUpdateNotes: (Topic, String) -> Unit,
    onDelete: (Topic) -> Unit
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
