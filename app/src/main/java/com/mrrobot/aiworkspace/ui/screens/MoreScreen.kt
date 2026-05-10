package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title

@Composable
fun MoreScreen(navController: NavController) {
    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item {
                Title("More")
                Subtitle("Access workspace tools, files, marketplace, settings, and profile.")

                Spacer(Modifier.height(14.dp))

                MoreCard(
                    title = "Live Terminal",
                    subtitle = "View simulated build logs and command output.",
                    button = "Open Terminal",
                    onClick = { navController.navigate(Route.Terminal.path) }
                )

                MoreCard(
                    title = "File Manager",
                    subtitle = "Browse project files and Stitch design references.",
                    button = "Open Files",
                    onClick = { navController.navigate(Route.Files.path) }
                )

                MoreCard(
                    title = "Marketplace",
                    subtitle = "Manage models, tools, agents, workflows, and integrations.",
                    button = "Open Marketplace",
                    onClick = { navController.navigate(Route.Market.path) }
                )

                MoreCard(
                    title = "Settings",
                    subtitle = "Configure OpenRouter, model selection, and Auto/Dark/Light theme.",
                    button = "Open Settings",
                    onClick = { navController.navigate(Route.Settings.path) }
                )

                MoreCard(
                    title = "Profile",
                    subtitle = "View workspace identity, stats, and export summary.",
                    button = "Open Profile",
                    onClick = { navController.navigate(Route.Profile.path) }
                )
            }
        }
    }
}

@Composable
private fun MoreCard(
    title: String,
    subtitle: String,
    button: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
    ) {
        Title(title)
        Subtitle(subtitle)

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onClick
        ) {
            Text(button)
        }
    }

    Spacer(Modifier.height(12.dp))
}
