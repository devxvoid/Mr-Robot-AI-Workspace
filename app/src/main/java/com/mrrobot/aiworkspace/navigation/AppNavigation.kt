package com.mrrobot.aiworkspace.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mrrobot.aiworkspace.ui.dashboard.DashboardScreen
import com.mrrobot.aiworkspace.ui.screens.*
import com.mrrobot.aiworkspace.ui.chat.ChatScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Dashboard.route
    ) {

        composable(
            AppRoutes.Dashboard.route
        ) {
            DashboardScreen()
        }

        composable(
            AppRoutes.Chat.route
        ) {
            ChatScreen()
        }

        composable(
            AppRoutes.Agents.route
        ) {
            AgentsScreen()
        }

        composable(
            AppRoutes.Workflow.route
        ) {
            WorkflowScreen()
        }

        composable(
            AppRoutes.Terminal.route
        ) {
            TerminalScreen()
        }

        composable(
            AppRoutes.Files.route
        ) {
            FileManagerScreen()
        }

        composable(
            AppRoutes.Marketplace.route
        ) {
            MarketplaceScreen()
        }

        composable(
            AppRoutes.Settings.route
        ) {
            SettingsScreen()
        }

        composable(
            AppRoutes.Profile.route
        ) {
            ProfileScreen()
        }
    }
}
