package com.mrrobot.aiworkspace.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.R
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

/**
 * Top-level navigation destinations for the Mr. Robot workspace.
 *
 * The root composable is a single M3 [Scaffold] whose [bottomBar] hosts
 * the [NavigationBar]. Each screen then hosts its own nested Scaffold
 * with a dedicated top app bar (and optional FAB), which keeps M3
 * concerns local to each screen while the parent scaffold is
 * responsible for the shared navigation chrome.
 */
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

private val BottomNavItems = listOf(
    Route.Welcome,
    Route.Chat,
    Route.Agents,
    Route.Workflow,
    Route.More
)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val currentRoute = navController
                .currentBackStackEntryAsState()
                .value
                ?.destination
                ?.route

            NavigationBar(
                // No custom container color so NavigationBar picks up the
                // theme's M3 surfaceContainer token natively. 3dp tonal
                // elevation matches the M3 reference implementation.
                tonalElevation = 3.dp
            ) {
                BottomNavItems.forEach { route ->
                    NavigationBarItem(
                        selected = isBottomItemSelected(currentRoute, route),
                        onClick = {
                            navController.navigate(route.path) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = route.iconRes),
                                contentDescription = route.label
                            )
                        },
                        label = { Text(route.label, maxLines = 1) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors()
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Welcome.path,
            modifier = Modifier
        ) {
            composable(Route.Welcome.path) { WelcomeScreen(navController, innerPadding) }
            composable(Route.Chat.path) { ChatScreen(parentPadding = innerPadding) }
            composable(Route.Agents.path) { AgentsScreen(parentPadding = innerPadding) }
            composable(Route.Workflow.path) { WorkflowScreen(parentPadding = innerPadding) }
            composable(Route.More.path) { MoreScreen(navController, innerPadding) }

            composable(Route.Terminal.path) { TerminalScreen(parentPadding = innerPadding) }
            composable(Route.Files.path) { FileManagerScreen(parentPadding = innerPadding) }
            composable(Route.Market.path) { MarketplaceScreen(parentPadding = innerPadding) }
            composable(Route.Settings.path) { SettingsScreen(parentPadding = innerPadding) }
            composable(Route.Profile.path) { ProfileScreen(parentPadding = innerPadding) }
        }
    }
}

/**
 * Returns `true` when [route] is the active tab. Also treats any
 * destination reachable from the "More" hub (Terminal, Files, Market,
 * Settings, Profile) as selecting the More tab, so the user always
 * has a visible anchor in the bottom nav.
 */
private fun isBottomItemSelected(
    currentRoute: String?,
    route: Route
): Boolean {
    if (currentRoute == route.path) return true

    val moreDestinations = setOf(
        Route.Terminal.path,
        Route.Files.path,
        Route.Market.path,
        Route.Settings.path,
        Route.Profile.path
    )

    return route == Route.More && currentRoute in moreDestinations
}

/**
 * Extracts just the bottom navigation padding so nested screen
 * scaffolds can offset their content without re-applying the status
 * bar inset (which their own top bars already handle).
 */
fun PaddingValues.bottomOnly(): PaddingValues = PaddingValues(
    bottom = calculateBottomPadding()
)

/**
 * Merges the parent [PaddingValues] (from the root Scaffold) with the
 * child Scaffold's inner padding, adding the screen's standard
 * horizontal 16dp rhythm and a bit of breathing room at the bottom.
 *
 * Child screens set `contentWindowInsets = WindowInsets(0)` on their
 * nested Scaffolds, so [innerPadding] here is effectively only the
 * space consumed by the child's TopAppBar. The parent's
 * NavigationBar inset is in [parentPadding] and is added on the
 * bottom only.
 */
fun mergedScreenPadding(
    innerPadding: PaddingValues,
    parentPadding: PaddingValues,
    horizontalEdge: androidx.compose.ui.unit.Dp = 16.dp,
    topExtra: androidx.compose.ui.unit.Dp = 8.dp,
    bottomExtra: androidx.compose.ui.unit.Dp = 24.dp
): PaddingValues = PaddingValues(
    start = horizontalEdge,
    end = horizontalEdge,
    top = innerPadding.calculateTopPadding() + topExtra,
    bottom = parentPadding.calculateBottomPadding() + bottomExtra
)
