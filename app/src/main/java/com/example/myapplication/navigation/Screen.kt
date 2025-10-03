package com.example.myapplication.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object SubjectTracker : Screen("subject_tracker")
    object TodoList : Screen("todo_list")
    object RevisionPlanner : Screen("revision_planner")
    object ErrorBook : Screen("error_book")
    object Settings : Screen("settings")
}
