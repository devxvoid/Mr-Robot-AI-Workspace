package com.mrrobot.aiworkspace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mrrobot.aiworkspace.ui.theme.*

data class BottomItem(
    val route: String,
    val label: String
)

@Composable
fun PremiumBottomBar(
    navController: NavController
) {

    val items = listOf(
        BottomItem("welcome", "Home"),
        BottomItem("chat", "AI"),
        BottomItem("agents", "Agents"),
        BottomItem("workflow", "Flow"),
        BottomItem("terminal", "Term"),
        BottomItem("files", "Files"),
        BottomItem("market", "Market"),
        BottomItem("settings", "Settings"),
        BottomItem("profile", "Profile")
    )

    NavigationBar(
        containerColor = BgSecondary
    ) {

        val current =
            navController.currentBackStackEntryAsState()
                .value
                ?.destination
                ?.route

        items.forEach { item ->

            NavigationBarItem(
                selected = current == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                label = {
                    Text(item.label)
                },
                icon = {},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonBlue,
                    selectedTextColor = NeonBlue,
                    indicatorColor = NeonBlue.copy(alpha = .15f),
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
