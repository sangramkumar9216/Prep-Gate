package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.navigation.Screen

data class NavigationItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
)

@Composable
fun NavigationDrawerContent(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationItems = listOf(
        NavigationItem(Screen.Dashboard, "Dashboard", Icons.Default.Home),
        NavigationItem(Screen.SubjectTracker, "Subject Tracker", Icons.Default.Book),
        NavigationItem(Screen.TodoList, "To-Do List", Icons.Default.List),
        NavigationItem(Screen.RevisionPlanner, "Revision Planner", Icons.Default.Schedule),
        NavigationItem(Screen.ErrorBook, "Error Book", Icons.Default.Error),
        NavigationItem(Screen.Settings, "Settings", Icons.Default.Settings)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "GATE CSE 2026",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Navigation Items
        LazyColumn {
            items(navigationItems) { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selected = selectedScreen == item.screen,
                    onClick = {
                        onScreenSelected(item.screen)
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            text = "Good luck with your preparation!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
