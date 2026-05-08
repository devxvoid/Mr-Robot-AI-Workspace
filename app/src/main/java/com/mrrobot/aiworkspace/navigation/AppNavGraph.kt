package com.mrrobot.aiworkspace.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.ui.components.NeonCyan
import com.mrrobot.aiworkspace.ui.components.NeonPurple
import com.mrrobot.aiworkspace.ui.screens.*

sealed class Route(val path: String, val label: String, val icon: String) {
    object Welcome : Route("welcome", "Home", "⌂")
    object Chat : Route("chat", "Chat", "AI")
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

    val primaryItems = listOf(
        Route.Welcome,
        Route.Chat,
        Route.Agents,
        Route.Workflow,
        Route.Terminal
    )

    val secondaryItems = listOf(
        Route.Files,
        Route.Market,
        Route.Settings,
        Route.Profile
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFF030712).copy(alpha = 0.96f)
                            )
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                val current = nav.currentBackStackEntryAsState().value?.destination?.route

                NavigationBar(
                    containerColor = Color(0xFF0B1020).copy(alpha = 0.96f),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                ) {
                    primaryItems.forEach { route ->
                        PremiumNavItem(
                            selected = current == route.path,
                            route = route,
                            onClick = {
                                nav.navigate(route.path) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                NavigationBar(
                    containerColor = Color(0xFF111827).copy(alpha = 0.86f),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    secondaryItems.forEach { route ->
                        PremiumNavItem(
                            selected = current == route.path,
                            route = route,
                            compact = true,
                            onClick = {
                                nav.navigate(route.path) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
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

@Composable
private fun RowScope.PremiumNavItem(
    selected: Boolean,
    route: Route,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Surface(
                color = if (selected) NeonCyan.copy(alpha = 0.16f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = route.icon,
                    color = if (selected) NeonCyan else Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        },
        label = {
            Text(
                text = route.label,
                color = if (selected) NeonCyan else Color(0xFF9CA3AF),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = NeonCyan,
            selectedTextColor = NeonCyan,
            indicatorColor = NeonPurple.copy(alpha = 0.12f),
            unselectedIconColor = Color(0xFF9CA3AF),
            unselectedTextColor = Color(0xFF9CA3AF)
        )
    )
}
