package com.example.myapplication.ui.screens.todolist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.myapplication.data.entity.TodoPriority
import com.example.myapplication.domain.model.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val todos by viewModel.todos.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val filterType by viewModel.filterType.collectAsState()

    var showAddTodoDialog by remember { mutableStateOf(false) }
    var showEditTodoDialog by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "To-Do List",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { showAddTodoDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        FilterChips(
            currentFilter = filterType,
            onFilterSelected = { filter ->
                viewModel.setFilter(filter)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Todo List
        if (todos.isEmpty()) {
            EmptyStateCard(filterType = filterType)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos) { todo ->
                    TodoCard(
                        todo = todo,
                        onToggleCompletion = {
                            viewModel.toggleTodoCompletion(todo)
                        },
                        onEdit = {
                            selectedTodo = todo
                            showEditTodoDialog = true
                        },
                        onDelete = {
                            viewModel.deleteTodo(todo)
                        }
                    )
                }
            }
        }
    }

    // Add Todo Dialog
    if (showAddTodoDialog) {
        AddEditTodoDialog(
            subjects = subjects,
            onDismiss = { showAddTodoDialog = false },
            onConfirm = { title, description, subjectId, priority, dueDate ->
                viewModel.addTodo(title, description, subjectId, priority, dueDate)
                showAddTodoDialog = false
            }
        )
    }

    // Edit Todo Dialog
    if (showEditTodoDialog && selectedTodo != null) {
        AddEditTodoDialog(
            todo = selectedTodo,
            subjects = subjects,
            onDismiss = { 
                showEditTodoDialog = false
                selectedTodo = null
            },
            onConfirm = { title, description, subjectId, priority, dueDate ->
                selectedTodo?.let { todo ->
                    val updatedTodo = todo.copy(
                        title = title,
                        description = description,
                        subjectId = subjectId,
                        priority = priority,
                        dueDate = dueDate
                    )
                    viewModel.updateTodo(updatedTodo)
                }
                showEditTodoDialog = false
                selectedTodo = null
            }
        )
    }
}

@Composable
fun FilterChips(
    currentFilter: TodoFilterType,
    onFilterSelected: (TodoFilterType) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TodoFilterType.values()) { filter ->
            FilterChip(
                selected = currentFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.displayName) }
            )
        }
    }
}

@Composable
fun EmptyStateCard(filterType: TodoFilterType) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Checklist,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = when (filterType) {
                    TodoFilterType.ALL -> "No tasks yet"
                    TodoFilterType.PENDING -> "No pending tasks"
                    TodoFilterType.COMPLETED -> "No completed tasks"
                    TodoFilterType.TODAY -> "No tasks due today"
                    TodoFilterType.HIGH_PRIORITY -> "No high priority tasks"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (filterType) {
                    TodoFilterType.ALL -> "Add your first task to get started"
                    TodoFilterType.PENDING -> "All caught up! Great job!"
                    TodoFilterType.COMPLETED -> "Complete some tasks to see them here"
                    TodoFilterType.TODAY -> "No tasks scheduled for today"
                    TodoFilterType.HIGH_PRIORITY -> "No high priority tasks at the moment"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TodoCard(
    todo: Todo,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = todo.isDone,
                    onCheckedChange = { onToggleCompletion() }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (todo.isDone) 
                            MaterialTheme.colorScheme.onSurfaceVariant 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (todo.description != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todo.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriorityChip(priority = todo.priority)
                        
                        if (todo.dueDate != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            DueDateChip(dueDate = todo.dueDate)
                        }
                        
                        if (todo.subjectName != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            SubjectChip(subjectName = todo.subjectName)
                        }
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
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
fun PriorityChip(priority: TodoPriority) {
    val (color, text) = when (priority) {
        TodoPriority.HIGH -> MaterialTheme.colorScheme.error to "High"
        TodoPriority.MEDIUM -> MaterialTheme.colorScheme.primary to "Medium"
        TodoPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant to "Low"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DueDateChip(dueDate: Long) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateText = dateFormat.format(Date(dueDate))
    
    Surface(
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun SubjectChip(subjectName: String) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = subjectName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AddEditTodoDialog(
    todo: Todo? = null,
    subjects: List<com.example.myapplication.domain.model.Subject>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Long?, TodoPriority, Long?) -> Unit
) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var selectedSubjectId by remember { mutableStateOf(todo?.subjectId) }
    var selectedPriority by remember { mutableStateOf(todo?.priority ?: TodoPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf(todo?.dueDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (todo == null) "Add Task" else "Edit Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority Selection
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row {
                    TodoPriority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    onConfirm(
                        title, 
                        description.ifBlank { null }, 
                        selectedSubjectId, 
                        selectedPriority, 
                        dueDate
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text(if (todo == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private val TodoFilterType.displayName: String
    get() = when (this) {
        TodoFilterType.ALL -> "All"
        TodoFilterType.PENDING -> "Pending"
        TodoFilterType.COMPLETED -> "Completed"
        TodoFilterType.TODAY -> "Today"
        TodoFilterType.HIGH_PRIORITY -> "High Priority"
    }
