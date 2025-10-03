package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.dashboard.DashboardScreen
import com.example.myapplication.ui.screens.subjecttracker.SubjectTrackerScreen
import com.example.myapplication.ui.screens.todolist.TodoListScreen
import com.example.myapplication.ui.screens.revisionplanner.RevisionPlannerScreen
import com.example.myapplication.ui.screens.errorbook.ErrorBookScreen
import com.example.myapplication.ui.screens.settings.SettingsScreen

@Composable
fun GatePrepNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.SubjectTracker.route) {
            SubjectTrackerScreen()
        }
        composable(Screen.TodoList.route) {
            TodoListScreen()
        }
        composable(Screen.RevisionPlanner.route) {
            RevisionPlannerScreen()
        }
        composable(Screen.ErrorBook.route) {
            ErrorBookScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
