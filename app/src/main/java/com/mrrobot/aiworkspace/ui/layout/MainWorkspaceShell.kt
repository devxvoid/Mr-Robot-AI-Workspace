package com.mrrobot.aiworkspace.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.navigation.AppNavigation
import com.mrrobot.aiworkspace.navigation.AppRoutes

@Composable
fun MainWorkspaceShell() {

    val navController = rememberNavController()

    val items = listOf(
        AppRoutes.Dashboard,
        AppRoutes.Chat,
        AppRoutes.Agents,
        AppRoutes.Workflow,
        AppRoutes.Terminal,
        AppRoutes.Files,
        AppRoutes.Marketplace,
        AppRoutes.Settings,
        AppRoutes.Profile
    )

    val backStack by
        navController.currentBackStackEntryAsState()

    val currentRoute =
        backStack?.destination?.route

    Scaffold(

        containerColor = Color(0xFF020617),

        bottomBar = {

            NavigationBar(
                containerColor = Color(0xFF071427)
            ) {

                items.forEach { screen ->

                    NavigationBarItem(

                        selected =
                            currentRoute == screen.route,

                        onClick = {

                            navController.navigate(
                                screen.route
                            ) {
                                launchSingleTop = true
                            }
                        },

                        icon = {},

                        label = {

                            Text(
                                text = screen.route
                                    .replaceFirstChar {
                                        it.uppercase()
                                    }
                            )
                        }
                    )
                }
            }
        }

    ) { padding ->

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFF020617))
        ) {

            AppNavigation(navController)
        }
    }
}
