package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.TodoDao
import com.example.myapplication.data.dao.SubjectDao
import com.example.myapplication.data.entity.TodoEntity
import com.example.myapplication.data.entity.TodoPriority
import com.example.myapplication.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val subjectDao: SubjectDao
) {
    fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    subjectId = entity.subjectId,
                    subjectName = null, // Will be populated separately if needed
                    priority = entity.priority,
                    isDone = entity.isDone,
                    createdAt = entity.createdAt,
                    dueDate = entity.dueDate
                )
            }
        }
    }

    fun getPendingTodos(): Flow<List<Todo>> {
        return todoDao.getPendingTodos().map { entities ->
            entities.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    subjectId = entity.subjectId,
                    subjectName = null,
                    priority = entity.priority,
                    isDone = entity.isDone,
                    createdAt = entity.createdAt,
                    dueDate = entity.dueDate
                )
            }
        }
    }

    fun getCompletedTodos(): Flow<List<Todo>> {
        return todoDao.getCompletedTodos().map { entities ->
            entities.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    subjectId = entity.subjectId,
                    subjectName = null,
                    priority = entity.priority,
                    isDone = entity.isDone,
                    createdAt = entity.createdAt,
                    dueDate = entity.dueDate
                )
            }
        }
    }

    fun getTodosDueToday(endOfDay: Long): Flow<List<Todo>> {
        return todoDao.getTodosDueToday(endOfDay).map { entities ->
            entities.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    subjectId = entity.subjectId,
                    subjectName = null,
                    priority = entity.priority,
                    isDone = entity.isDone,
                    createdAt = entity.createdAt,
                    dueDate = entity.dueDate
                )
            }
        }
    }

    fun getTodosByPriority(priority: TodoPriority): Flow<List<Todo>> {
        return todoDao.getTodosByPriority(priority).map { entities ->
            entities.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    subjectId = entity.subjectId,
                    subjectName = null,
                    priority = entity.priority,
                    isDone = entity.isDone,
                    createdAt = entity.createdAt,
                    dueDate = entity.dueDate
                )
            }
        }
    }

    suspend fun getTodoById(id: Long): Todo? {
        val entity = todoDao.getTodoById(id) ?: return null
        return Todo(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            subjectId = entity.subjectId,
            subjectName = null,
            priority = entity.priority,
            isDone = entity.isDone,
            createdAt = entity.createdAt,
            dueDate = entity.dueDate
        )
    }

    suspend fun insertTodo(todo: TodoEntity): Long {
        return todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo)
    }

    suspend fun deleteAllTodos() {
        todoDao.deleteAllTodos()
    }
}
