package com.mrrobot.aiworkspace.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.ui.components.StoreBottomNav
import com.mrrobot.aiworkspace.ui.components.StoreBottomNavItem
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

sealed class Route(
    val path: String,
    val label: String,
    @DrawableRes val iconRes: Int
) {
    object Welcome : Route("welcome", "Home", R.drawable.ic_lucide_home)
    object Chat : Route("chat", "AI", R.drawable.ic_lucide_sparkles)
    object Agents : Route("agents", "Agents", R.drawable.ic_lucide_bot)
    object Workflow : Route("workflow", "Flow", R.drawable.ic_lucide_workflow)
    object More : Route("more", "More", R.drawable.ic_lucide_menu)
    object Terminal : Route("terminal", "Terminal", R.drawable.ic_lucide_terminal)
    object Files : Route("files", "Files", R.drawable.ic_lucide_folder)
    object Market : Route("market", "Store", R.drawable.ic_lucide_store)
    object Settings : Route("settings", "Settings", R.drawable.ic_lucide_settings)
    object Profile : Route("profile", "Profile", R.drawable.ic_lucide_user)
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val scheme = MaterialTheme.colorScheme

    val bottomItems = listOf(
        Route.Welcome,
        Route.Chat,
        Route.Agents,
        Route.Workflow,
        Route.More
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to scheme.background,
                    0.6f to scheme.surfaceContainerLow,
                    1.0f to scheme.surfaceContainerHigh
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                StoreBottomNav(
                    items = bottomItems.map { route ->
                        StoreBottomNavItem(
                            iconRes = route.iconRes,
                            label = route.label,
                            selected = isBottomItemSelected(
                                currentRoute = currentRoute,
                                route = route
                            ),
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
                )
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
                composable(Route.More.path) { MoreScreen(navController) }
                composable(Route.Terminal.path) { TerminalScreen() }
                composable(Route.Files.path) { FileManagerScreen() }
                composable(Route.Market.path) { MarketplaceScreen() }
                composable(Route.Settings.path) { SettingsScreen() }
                composable(Route.Profile.path) { ProfileScreen() }
            }
        }
    }
}

private fun isBottomItemSelected(
    currentRoute: String?,
    route: Route
): Boolean {
    if (currentRoute == route.path) return true

    val moreRoutes = setOf(
        Route.Terminal.path,
        Route.Files.path,
        Route.Market.path,
        Route.Settings.path,
        Route.Profile.path
    )

    return route == Route.More && currentRoute in moreRoutes
}
