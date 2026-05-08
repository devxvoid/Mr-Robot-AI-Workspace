package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun MoreScreen(nav: NavController) {
    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                PageTitle("Command Modules")
                Subtitle("Access terminal, files, marketplace, settings, and profile without breaking the bottom navigation.")
                Spacer(Modifier.height(16.dp))

                MoreItem(">_", "Live Terminal", "Safe command simulation and logs") {
                    nav.navigate(Route.Terminal.path)
                }

                MoreItem("▣", "File Manager", "Browse project files and design assets") {
                    nav.navigate(Route.Files.path)
                }

                MoreItem("✦", "Marketplace", "Models, tools, agents, integrations") {
                    nav.navigate(Route.Market.path)
                }

                MoreItem("⚙", "Settings", "OpenRouter, model, Auto/Dark/Light theme") {
                    nav.navigate(Route.Settings.path)
                }

                MoreItem("◎", "Profile", "Workspace identity and status") {
                    nav.navigate(Route.Profile.path)
                }
            }
        }
    }
}

@Composable
private fun MoreItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$icon  $title",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Subtitle(subtitle)
            }
            Text("→", color = MaterialTheme.colorScheme.primary)
        }
    }
}
