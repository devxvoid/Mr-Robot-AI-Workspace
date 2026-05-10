package com.mrrobot.aiworkspace.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.ui.screens.*

sealed class Route(val path: String, val label: String, val icon: String) {
    object Welcome : Route("welcome", "Home", "⌂")
    object Chat : Route("chat", "AI", "AI")
    object Agents : Route("agents", "Agents", "◆")
    object Workflow : Route("workflow", "Flow", "↯")
    object Terminal : Route("terminal", "Term", ">_")
    object Files : Route("files", "Files", "▣")
    object Market : Route("market", "Market", "✦")
    object Settings : Route("settings", "Settings", "⚙")
    object Profile : Route("profile", "Profile", "◎")
}

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()

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
            NavigationBar(
                modifier = Modifier.height(74.dp),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 0.dp
            ) {
                val current = nav.currentBackStackEntryAsState().value?.destination?.route

                items.forEach { route ->
                    NavigationBarItem(
                        selected = current == route.path,
                        onClick = {
                            nav.navigate(route.path) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(
                                text = route.icon,
                                fontWeight = FontWeight.Bold,
                                color = if (current == route.path) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        },
                        label = {
                            Text(
                                text = route.label,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Route.Welcome.path,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Welcome.path) { WelcomeScreen(nav) }
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
