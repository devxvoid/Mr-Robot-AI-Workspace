package com.mrrobot.aiworkspace.navigation

sealed class AppRoutes(
    val route: String
) {

    data object Dashboard : AppRoutes("dashboard")
    data object Chat : AppRoutes("chat")
    data object Agents : AppRoutes("agents")
    data object Workflow : AppRoutes("workflow")
    data object Terminal : AppRoutes("terminal")
    data object Files : AppRoutes("files")
    data object Marketplace : AppRoutes("marketplace")
    data object Settings : AppRoutes("settings")
    data object Profile : AppRoutes("profile")
}
