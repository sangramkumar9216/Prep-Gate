package com.example.myapplication.di

import com.example.myapplication.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSubjectRepository(
        subjectRepository: SubjectRepository
    ): SubjectRepository = subjectRepository

    @Provides
    @Singleton
    fun provideTopicRepository(
        topicRepository: TopicRepository
    ): TopicRepository = topicRepository

    @Provides
    @Singleton
    fun provideTodoRepository(
        todoRepository: TodoRepository
    ): TodoRepository = todoRepository

    @Provides
    @Singleton
    fun provideGoalRepository(
        goalRepository: GoalRepository
    ): GoalRepository = goalRepository
}
