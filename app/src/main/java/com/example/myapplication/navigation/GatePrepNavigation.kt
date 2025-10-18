package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.dashboard.DashboardScreen
import com.example.myapplication.ui.screens.errorbook.ErrorBookScreen
import com.example.myapplication.ui.screens.revisionplanner.RevisionPlannerScreen
import com.example.myapplication.ui.screens.settings.SettingsScreen
import com.example.myapplication.ui.screens.subjecttracker.SubjectTrackerScreen
import com.example.myapplication.ui.screens.todolist.TodoListScreen

@Composable
fun GatePrepNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(onMenuClick = onMenuClick)
        }
        composable(Screen.SubjectTracker.route) {
            // THIS IS THE CHANGE: Pass the menu click handler here
            SubjectTrackerScreen(onMenuClick = onMenuClick)
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