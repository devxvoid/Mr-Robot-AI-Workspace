package com.mrrobot.aiworkspace.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.ui.screens.*

sealed class Route(val path: String, val label: String) {
    object Welcome : Route("welcome", "Home")
    object Chat : Route("chat", "Chat")
    object Agents : Route("agents", "Agents")
    object Workflow : Route("workflow", "Flow")
    object Terminal : Route("terminal", "Terminal")
    object Files : Route("files", "Files")
    object Market : Route("market", "Market")
    object Settings : Route("settings", "Settings")
    object Profile : Route("profile", "Profile")
}

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()
    val items = listOf(Route.Welcome, Route.Chat, Route.Agents, Route.Workflow, Route.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current = nav.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { route ->
                    NavigationBarItem(
                        selected = current == route.path,
                        onClick = { nav.navigate(route.path) { launchSingleTop = true } },
                        label = { Text(route.label) },
                        icon = { Text("●") }
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
