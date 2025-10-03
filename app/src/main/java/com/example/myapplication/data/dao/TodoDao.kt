package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.entity.TodoEntity
import com.example.myapplication.data.entity.TodoPriority
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isDone = 0 ORDER BY createdAt DESC")
    fun getPendingTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isDone = 1 ORDER BY createdAt DESC")
    fun getCompletedTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE dueDate IS NOT NULL AND dueDate <= :endOfDay ORDER BY dueDate ASC")
    fun getTodosDueToday(endOfDay: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTodosByPriority(priority: TodoPriority): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun getTodosBySubjectId(subjectId: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()
}
