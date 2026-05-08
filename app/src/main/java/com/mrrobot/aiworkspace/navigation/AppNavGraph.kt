package com.mrrobot.aiworkspace.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrrobot.aiworkspace.ui.screens.*

@Composable
fun AppNavGraph() {

    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen()
        }

        composable("chat") {
            ChatScreen()
        }

        composable("agents") {
            AgentsScreen()
        }

        composable("workflow") {
            WorkflowScreen()
        }

        composable("terminal") {
            TerminalScreen()
        }

        composable("files") {
            FileManagerScreen()
        }

        composable("market") {
            MarketplaceScreen()
        }

        composable("profile") {
            ProfileScreen()
        }

        composable("settings") {
            SettingsScreen()
        }

        composable("more") {
            MoreScreen()
        }
    }
}
