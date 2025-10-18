package com.example.myapplication.data.database

import com.example.myapplication.data.entity.SubjectEntity
import com.example.myapplication.data.entity.TopicEntity
import com.example.myapplication.data.entity.TopicStatus
import com.example.myapplication.data.repository.SubjectRepository
import com.example.myapplication.data.repository.TopicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository
) {

    fun initializeDatabase(scope: CoroutineScope) {
        scope.launch {
            // Check if subjects already exist
            val existingSubjects = subjectRepository.getAllSubjects().first()

            if (existingSubjects.isEmpty()) {
                seedInitialData()
            }
        }
    }

    private suspend fun seedInitialData() {
        // Create subjects
        val subjects = listOf(
            SubjectEntity(name = "Data Structures & Algorithms", color = "#FF6B6B"),
            SubjectEntity(name = "Database Management System", color = "#4ECDC4"),
            SubjectEntity(name = "Operating System", color = "#45B7D1"),
            SubjectEntity(name = "Computer Networks", color = "#96CEB4"),
            SubjectEntity(name = "Theory of Computation", color = "#FFEAA7"),
            SubjectEntity(name = "Compiler Design", color = "#DDA0DD"),
            SubjectEntity(name = "Aptitude", color = "#98D8C8")
        )

        // Insert subjects one by one to reliably get their generated IDs
        val subjectIds = subjects.map { subjectRepository.insertSubject(it) }

        // Create topics for each subject
        val topics = mutableListOf<TopicEntity>()

        // DSA Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[0], title = "Arrays and Strings", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Linked Lists", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Stacks and Queues", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Trees and Binary Trees", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Graphs", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Sorting Algorithms", status = TopicStatus.PENDING),
            // THIS IS THE FIX: Corrected "TopicS tatus.PENDING" to "TopicStatus.PENDING"
            TopicEntity(subjectId = subjectIds[0], title = "Searching Algorithms", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Dynamic Programming", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Greedy Algorithms", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[0], title = "Backtracking", status = TopicStatus.PENDING)
        ))

        // DBMS Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[1], title = "ER Model and Relational Model", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "SQL Queries", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "Normalization", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "Transaction Management", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "Concurrency Control", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "Database Recovery", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "Indexing and Hashing", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[1], title = "Query Processing", status = TopicStatus.PENDING)
        ))

        // OS Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[2], title = "Process Management", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "Threads and Concurrency", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "CPU Scheduling", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "Synchronization", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "Deadlocks", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "Memory Management", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "Virtual Memory", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[2], title = "File Systems", status = TopicStatus.PENDING)
        ))

        // CN Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[3], title = "Network Models and Protocols", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[3], title = "Physical Layer", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[3], title = "Data Link Layer", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[3], title = "Network Layer", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[3], title = "Transport Layer", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[3], title = "Application Layer", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[3], title = "Network Security", status = TopicStatus.PENDING)
        ))

        // TOC Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[4], title = "Finite Automata", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[4], title = "Regular Expressions", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[4], title = "Context-Free Grammars", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[4], title = "Pushdown Automata", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[4], title = "Turing Machines", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[4], title = "Decidability", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[4], title = "Complexity Theory", status = TopicStatus.PENDING)
        ))

        // Compiler Design Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[5], title = "Lexical Analysis", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[5], title = "Syntax Analysis", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[5], title = "Semantic Analysis", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[5], title = "Intermediate Code Generation", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[5], title = "Code Optimization", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[5], title = "Code Generation", status = TopicStatus.PENDING)
        ))

        // Aptitude Topics
        topics.addAll(listOf(
            TopicEntity(subjectId = subjectIds[6], title = "Quantitative Aptitude", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[6], title = "Verbal Ability", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[6], title = "Logical Reasoning", status = TopicStatus.PENDING),
            TopicEntity(subjectId = subjectIds[6], title = "Analytical Reasoning", status = TopicStatus.PENDING)
        ))

        // Insert all topics
        topicRepository.insertTopics(topics)
    }
}