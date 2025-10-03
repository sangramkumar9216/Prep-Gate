package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.GoalDao
import com.example.myapplication.data.entity.GoalEntity
import com.example.myapplication.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    fun getGoalsForDate(date: String): Flow<List<Goal>> {
        return goalDao.getGoalsForDate(date).map { entities ->
            entities.map { entity ->
                Goal(
                    id = entity.id,
                    title = entity.title,
                    isDoneForDate = entity.isDoneForDate,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { entities ->
            entities.map { entity ->
                Goal(
                    id = entity.id,
                    title = entity.title,
                    isDoneForDate = entity.isDoneForDate,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    suspend fun getGoalById(id: Long): Goal? {
        val entity = goalDao.getGoalById(id) ?: return null
        return Goal(
            id = entity.id,
            title = entity.title,
            isDoneForDate = entity.isDoneForDate,
            createdAt = entity.createdAt
        )
    }

    suspend fun insertGoal(goal: GoalEntity): Long {
        return goalDao.insertGoal(goal)
    }

    suspend fun insertGoals(goals: List<GoalEntity>) {
        goalDao.insertGoals(goals)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }

    suspend fun deleteAllGoals() {
        goalDao.deleteAllGoals()
    }
}
