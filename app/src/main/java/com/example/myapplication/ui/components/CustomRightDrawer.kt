package com.example.myapplication.ui.components

import androidx.compose.ui.Alignment
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CustomRightDrawer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // Let the drawer take up 85% of the screen width
    val drawerWidth = screenWidth * 0.85f

    // Animate the drawer's horizontal position (offset)
    val drawerOffset by animateDpAsState(
        targetValue = if (drawerState.isOpen) 0.dp else drawerWidth,
        animationSpec = tween(durationMillis = 400),
        label = "DrawerOffset"
    )

    // Scope to launch coroutines for closing the drawer
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. Main Content ---
        // This is the content of your regular screens (Dashboard, etc.)
        Box(modifier = Modifier.fillMaxSize()) {
            mainContent()
        }

        // --- 2. Scrim (the dimming overlay) ---
        // This appears only when the drawer is open
        if (drawerState.isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        // Use an empty interaction source to remove the ripple effect
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // When the scrim is clicked, close the drawer
                        scope.launch { drawerState.close() }
                    }
            )
        }

        // --- 3. Drawer Content ---
        // This is the actual drawer that slides in from the right
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidth)
                .align(Alignment.CenterEnd) // Aligns the drawer to the right edge of the screen
                .offset(x = drawerOffset)    // Animates the position from off-screen to on-screen
                .background(MaterialTheme.colorScheme.surface)
                .clickable(enabled = false, onClick = {}) // Prevents clicks from passing through to the scrim
        ) {
            drawerContent()
        }
    }
}