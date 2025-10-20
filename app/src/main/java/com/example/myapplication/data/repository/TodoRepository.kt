package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.TodoDao
import com.example.myapplication.data.dao.SubjectDao
import com.example.myapplication.data.entity.TodoEntity
import com.example.myapplication.data.entity.TodoPriority
import com.example.myapplication.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val subjectDao: SubjectDao
) {

    /**
     * Combines a list of TodoEntities with a list of SubjectEntities to create
     * a list of Todo domain models, populating the subjectName field.
     */
    private fun mapTodosWithSubjects(
        todoEntities: List<TodoEntity>,
        subjectEntities: List<com.example.myapplication.data.entity.SubjectEntity>
    ): List<Todo> {
        val subjectMap = subjectEntities.associateBy { it.id }
        return todoEntities.map { entity ->
            Todo(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                subjectId = entity.subjectId,
                // Here's the magic: Find the subject name from the map
                subjectName = entity.subjectId?.let { subjectMap[it]?.name },
                priority = entity.priority,
                isDone = entity.isDone,
                createdAt = entity.createdAt,
                dueDate = entity.dueDate
            )
        }
    }

    // --- MODIFIED FUNCTIONS ---

    fun getAllTodos(): Flow<List<Todo>> {
        return combine(todoDao.getAllTodos(), subjectDao.getAllSubjects()) { todos, subjects ->
            mapTodosWithSubjects(todos, subjects)
        }
    }

    fun getPendingTodos(): Flow<List<Todo>> {
        return combine(todoDao.getPendingTodos(), subjectDao.getAllSubjects()) { todos, subjects ->
            mapTodosWithSubjects(todos, subjects)
        }
    }

    fun getCompletedTodos(): Flow<List<Todo>> {
        return combine(todoDao.getCompletedTodos(), subjectDao.getAllSubjects()) { todos, subjects ->
            mapTodosWithSubjects(todos, subjects)
        }
    }

    fun getTodosDueToday(endOfDay: Long): Flow<List<Todo>> {
        return combine(todoDao.getTodosDueToday(endOfDay), subjectDao.getAllSubjects()) { todos, subjects ->
            mapTodosWithSubjects(todos, subjects)
        }
    }

    fun getTodosByPriority(priority: TodoPriority): Flow<List<Todo>> {
        return combine(todoDao.getTodosByPriority(priority), subjectDao.getAllSubjects()) { todos, subjects ->
            mapTodosWithSubjects(todos, subjects)
        }
    }

    // --- UNMODIFIED FUNCTIONS (for single get/insert/update/delete) ---

    suspend fun getTodoById(id: Long): Todo? {
        val entity = todoDao.getTodoById(id) ?: return null
        val subject = entity.subjectId?.let { subjectDao.getSubjectById(it) }
        return Todo(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            subjectId = entity.subjectId,
            subjectName = subject?.name,
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