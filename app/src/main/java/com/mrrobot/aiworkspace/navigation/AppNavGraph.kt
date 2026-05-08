package com.mrrobot.aiworkspace.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mrrobot.aiworkspace.ui.screens.MoreScreen
import com.mrrobot.aiworkspace.ui.screens.ProfileScreen
import com.mrrobot.aiworkspace.ui.screens.SettingsScreen
import com.mrrobot.aiworkspace.ui.screens.TerminalScreen
import com.mrrobot.aiworkspace.ui.screens.WelcomeScreen
import com.mrrobot.aiworkspace.ui.screens.WorkflowScreen

sealed class Route(val path: String, val label: String, val icon: String) {
    object Welcome : Route("welcome", "Home", "⌂")
    object Chat : Route("chat", "AI", "AI")
    object Agents : Route("agents", "Agents", "◆")
    object Workflow : Route("workflow", "Flow", "↯")
    object More : Route("more", "More", "☰")
    object Terminal : Route("terminal", "Terminal", ">_")
    object Files : Route("files", "Files", "▣")
    object Market : Route("market", "Market", "✦")
    object Settings : Route("settings", "Settings", "⚙")
    object Profile : Route("profile", "Profile", "◎")
}

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()

    val bottomItems = listOf(
        Route.Welcome,
        Route.Chat,
        Route.Agents,
        Route.Workflow,
        Route.More
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(76.dp),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 0.dp
            ) {
                val current = nav.currentBackStackEntryAsState().value?.destination?.route

                bottomItems.forEach { route ->
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
                                fontWeight = FontWeight.Bold
                            )
                        },
                        label = {
                            Text(
                                text = route.label,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
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
            composable(Route.More.path) { MoreScreen(nav) }
            composable(Route.Terminal.path) { TerminalScreen() }
            composable(Route.Files.path) { FileManagerScreen() }
            composable(Route.Market.path) { MarketplaceScreen() }
            composable(Route.Settings.path) { SettingsScreen() }
            composable(Route.Profile.path) { ProfileScreen() }
        }
    }
}
