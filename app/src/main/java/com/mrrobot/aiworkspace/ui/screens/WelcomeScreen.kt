package com.mrrobot.aiworkspace.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.data.SettingsStore
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.CyberButton
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.PremiumMetric
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.StatusPill
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(nav: NavController) {
    val context = LocalContext.current
    val settingsStore = remember {
        SettingsStore(context.applicationContext)
    }
    val coroutineScope = rememberCoroutineScope()

    val settings by settingsStore.settingsFlow.collectAsState(
        initial = AppSettings()
    )

    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item {
                HomeHeader()

                Spacer(Modifier.height(16.dp))

                QuickThemeCard(
                    selected = settings.themeMode,
                    onSelected = { theme ->
                        coroutineScope.launch {
                            settingsStore.saveSettings(
                                apiKey = settings.apiKey,
                                model = settings.model,
                                themeMode = theme
                            )
                        }
                    }
                )

                Spacer(Modifier.height(14.dp))

                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPill("Cyber")
                        StatusPill("Hacker")
                        StatusPill("Assets")
                    }

                    Spacer(Modifier.height(14.dp))

                    Title("Command Center")

                    Subtitle(
                        "Use quick switching for Auto, Light, and Dark directly from Home. Cyber and Hacker remain available inside Settings."
                    )

                    Spacer(Modifier.height(14.dp))

                    CyberButton("Launch AI Chat") {
                        nav.navigate(Route.Chat.path)
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Agents.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Open Agent System")
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Workflow.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Build Workflow")
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Screens",
                            value = "20+",
                            description = "Workspace tools"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Themes",
                            value = "5",
                            description = "Auto/Dark/Light/Cyber/Hacker"
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "AI",
                            value = "OR",
                            description = "OpenRouter"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Agents",
                            value = "5",
                            description = "Role prompts"
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mr. Robot AI\nWorkspace",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "A professional Android command center for AI chat, agents, workflows, terminal logs, marketplace tools, and OpenRouter models.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}

@Composable
private fun QuickThemeCard(
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    GlassCard {
        Title("Quick Theme")

        Spacer(Modifier.height(6.dp))

        Subtitle("Switch Auto, Light, or Dark directly from Home.")

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemeQuickTile(
                title = "Auto",
                mode = AppThemeMode.Auto,
                selected = selected,
                onSelected = onSelected,
                iconRes = R.drawable.ic_lucide_sun_moon,
                modifier = Modifier.weight(1f)
            )

            ThemeQuickTile(
                title = "Light",
                mode = AppThemeMode.Light,
                selected = selected,
                onSelected = onSelected,
                iconRes = R.drawable.ic_lucide_sun,
                modifier = Modifier.weight(1f)
            )

            ThemeQuickTile(
                title = "Dark",
                mode = AppThemeMode.Dark,
                selected = selected,
                onSelected = onSelected,
                iconRes = R.drawable.ic_lucide_moon,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeQuickTile(
    title: String,
    mode: AppThemeMode,
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    val isSelected = selected == mode

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.76f)
    }

    Surface(
        modifier = modifier.clickable {
            onSelected(mode)
        },
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = title,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}
