package com.example.myapplication.ui.screens.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.TodoEntity
import com.example.myapplication.data.entity.TodoPriority
import com.example.myapplication.data.repository.TodoRepository
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.domain.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _filterType = MutableStateFlow(TodoFilterType.ALL)
    val filterType: StateFlow<TodoFilterType> = _filterType.asStateFlow()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _subjects = MutableStateFlow<List<com.example.myapplication.domain.model.Subject>>(emptyList())
    val subjects: StateFlow<List<com.example.myapplication.domain.model.Subject>> = _subjects.asStateFlow()

    init {
        loadSubjects()
        loadTodos()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.getAllSubjects().collect { subjectsList ->
                _subjects.value = subjectsList
            }
        }
    }

    private fun loadTodos() {
        viewModelScope.launch {
            when (_filterType.value) {
                TodoFilterType.ALL -> {
                    todoRepository.getAllTodos().collect { todosList ->
                        _todos.value = todosList
                    }
                }
                TodoFilterType.PENDING -> {
                    todoRepository.getPendingTodos().collect { todosList ->
                        _todos.value = todosList
                    }
                }
                TodoFilterType.COMPLETED -> {
                    todoRepository.getCompletedTodos().collect { todosList ->
                        _todos.value = todosList
                    }
                }
                TodoFilterType.TODAY -> {
                    val endOfDay = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.timeInMillis
                    
                    todoRepository.getTodosDueToday(endOfDay).collect { todosList ->
                        _todos.value = todosList
                    }
                }
                TodoFilterType.HIGH_PRIORITY -> {
                    todoRepository.getTodosByPriority(TodoPriority.HIGH).collect { todosList ->
                        _todos.value = todosList
                    }
                }
            }
        }
    }

    fun setFilter(filterType: TodoFilterType) {
        _filterType.value = filterType
        loadTodos()
    }

    fun addTodo(
        title: String,
        description: String?,
        subjectId: Long?,
        priority: TodoPriority,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            val newTodo = TodoEntity(
                title = title,
                description = description,
                subjectId = subjectId,
                priority = priority,
                isDone = false,
                dueDate = dueDate
            )
            todoRepository.insertTodo(newTodo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            val todoEntity = TodoEntity(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                subjectId = todo.subjectId,
                priority = todo.priority,
                isDone = todo.isDone,
                createdAt = todo.createdAt,
                dueDate = todo.dueDate
            )
            todoRepository.updateTodo(todoEntity)
        }
    }

    fun toggleTodoCompletion(todo: Todo) {
        val updatedTodo = todo.copy(isDone = !todo.isDone)
        updateTodo(updatedTodo)
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            val todoEntity = TodoEntity(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                subjectId = todo.subjectId,
                priority = todo.priority,
                isDone = todo.isDone,
                createdAt = todo.createdAt,
                dueDate = todo.dueDate
            )
            todoRepository.deleteTodo(todoEntity)
        }
    }
}

enum class TodoFilterType {
    ALL,
    PENDING,
    COMPLETED,
    TODAY,
    HIGH_PRIORITY
}
