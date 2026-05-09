package com.mrrobot.aiworkspace.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.navigation.AppNavigation
import com.mrrobot.aiworkspace.navigation.AppRoutes

@Composable
fun MainWorkspaceShell() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        AppRoutes.Dashboard,
        AppRoutes.Chat,
        AppRoutes.Marketplace,
        AppRoutes.Settings
    )

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF071427)
            ) {
                bottomItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                popUpTo(AppRoutes.Dashboard.route) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = {
                            Text(
                                text = when (screen) {
                                    AppRoutes.Dashboard -> "Home"
                                    AppRoutes.Chat -> "AI"
                                    AppRoutes.Marketplace -> "Marketplace"
                                    AppRoutes.Settings -> "Settings"
                                    else -> screen.route
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = Color(0xFF00D4FF),
                            indicatorColor = Color(0x2200D4FF),
                            unselectedTextColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFF020617))
        ) {
            AppNavigation(navController)
        }
    }
}
