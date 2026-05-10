package com.mrrobot.aiworkspace.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.ui.screens.AgentsScreen
import com.mrrobot.aiworkspace.ui.screens.ChatScreen
import com.mrrobot.aiworkspace.ui.screens.FileManagerScreen
import com.mrrobot.aiworkspace.ui.screens.MarketplaceScreen
import com.mrrobot.aiworkspace.ui.screens.ProfileScreen
import com.mrrobot.aiworkspace.ui.screens.SettingsScreen
import com.mrrobot.aiworkspace.ui.screens.TerminalScreen
import com.mrrobot.aiworkspace.ui.screens.WelcomeScreen
import com.mrrobot.aiworkspace.ui.screens.WorkflowScreen

sealed class Route(
    val path: String,
    val label: String,
    val icon: String
) {
    object Welcome : Route("welcome", "Home", "⌂")
    object Chat : Route("chat", "AI", "✦")
    object Agents : Route("agents", "Agents", "◆")
    object Workflow : Route("workflow", "Flow", "↯")
    object Terminal : Route("terminal", "Term", ">_")
    object Files : Route("files", "Files", "▣")
    object Market : Route("market", "Store", "◈")
    object Settings : Route("settings", "Settings", "⚙")
    object Profile : Route("profile", "Profile", "◎")
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val items = listOf(
        Route.Welcome,
        Route.Chat,
        Route.Agents,
        Route.Workflow,
        Route.Terminal,
        Route.Files,
        Route.Market,
        Route.Settings,
        Route.Profile
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { route ->
                        BottomNavChip(
                            route = route,
                            selected = currentRoute == route.path,
                            onClick = {
                                navController.navigate(route.path) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Welcome.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Welcome.path) { WelcomeScreen(navController) }
            composable(Route.Chat.path) { ChatScreen() }
            composable(Route.Agents.path) { AgentsScreen() }
            composable(Route.Workflow.path) { WorkflowScreen() }
            composable(Route.Terminal.path) { TerminalScreen() }
            composable(Route.Files.path) { FileManagerScreen() }
            composable(Route.Market.path) { MarketplaceScreen() }
            composable(Route.Settings.path) { SettingsScreen() }
            composable(Route.Profile.path) { ProfileScreen() }
        }
    }
}

@Composable
private fun BottomNavChip(
    route: Route,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(84.dp)
            .height(56.dp)
            .clickable { onClick() },
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        } else {
            Color.Transparent
        },
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = route.icon,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = route.label,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
