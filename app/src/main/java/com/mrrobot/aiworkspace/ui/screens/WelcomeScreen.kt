package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.ElevatedGlassCard
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.IconBadge
import com.mrrobot.aiworkspace.ui.components.PremiumMetric
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.ui.components.StatBadge
import com.mrrobot.aiworkspace.ui.components.StatusPill
import com.mrrobot.aiworkspace.ui.components.StoreButton
import com.mrrobot.aiworkspace.ui.components.StoreButtonStyle
import com.mrrobot.aiworkspace.ui.theme.Elevation
import com.mrrobot.aiworkspace.ui.theme.Spacing
import com.mrrobot.aiworkspace.ui.theme.StoreColors

private data class QuickAction(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val route: String,
    val badge: String? = null
)

private data class CuratedEntry(
    val title: String,
    val summary: String,
    val iconRes: Int,
    val accent: Long,
    val route: String,
    val badge: String? = null
)

@Composable
fun WelcomeScreen(nav: NavController) {
    val quickActions = listOf(
        QuickAction(
            title = "AI Chat",
            subtitle = "Talk to your workspace",
            iconRes = R.drawable.ic_lucide_sparkles,
            route = Route.Chat.path
        ),
        QuickAction(
            title = "Agents",
            subtitle = "Role-based assistants",
            iconRes = R.drawable.ic_lucide_bot,
            route = Route.Agents.path,
            badge = "5"
        ),
        QuickAction(
            title = "Workflows",
            subtitle = "Build automations",
            iconRes = R.drawable.ic_lucide_workflow,
            route = Route.Workflow.path
        ),
        QuickAction(
            title = "Terminal",
            subtitle = "Logs & diagnostics",
            iconRes = R.drawable.ic_lucide_terminal,
            route = Route.Terminal.path
        )
    )

    val curated = listOf(
        CuratedEntry(
            title = "Marketplace",
            summary = "Discover premium developer tools curated for builders.",
            iconRes = R.drawable.ic_lucide_store,
            accent = 0xFF98CCF9,
            route = Route.Market.path,
            badge = "NEW"
        ),
        CuratedEntry(
            title = "File Manager",
            summary = "Organize project files, docs and screenshots in one place.",
            iconRes = R.drawable.ic_lucide_folder,
            accent = 0xFFD1BFE7,
            route = Route.Files.path
        ),
        CuratedEntry(
            title = "Profile",
            summary = "Your identity across Mr. Robot workspaces.",
            iconRes = R.drawable.ic_lucide_user,
            accent = 0xFFB8C8D9,
            route = Route.Profile.path
        ),
        CuratedEntry(
            title = "Settings",
            summary = "Tune providers, models and the store theme.",
            iconRes = R.drawable.ic_lucide_settings,
            accent = 0xFF66587B,
            route = Route.Settings.path
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.screenEdge,
                end = Spacing.screenEdge,
                top = 0.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            item { StoreAppHeader() }

            item { StoreWelcomeGreeting() }

            item { HeroBanner(onStartChat = { nav.navigate(Route.Chat.path) }) }

            item {
                SectionHeader(
                    title = "Quick actions"
                )
            }

            item {
                QuickActionGrid(
                    actions = quickActions,
                    onClick = { route -> nav.navigate(route) }
                )
            }

            item {
                SectionHeader(
                    title = "Workspace stats"
                )
            }

            item { StatsRow() }

            item {
                SectionHeader(
                    title = "Curated for you",
                    trailingAction = "See all",
                    onTrailingClick = { nav.navigate(Route.Market.path) }
                )
            }

            items(curated) { entry ->
                StoreTile(
                    title = entry.title,
                    description = entry.summary,
                    painterRes = entry.iconRes,
                    tint = Color(entry.accent),
                    badge = entry.badge,
                    onClick = { nav.navigate(entry.route) }
                )
            }

            item { Spacer(Modifier.height(Spacing.m)) }
        }
    }
}

@Composable
private fun StoreAppHeader() {
    Surface(
        color = StoreColors.glassSurface(),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = Spacing.m)
            .border(
                width = 1.dp,
                color = StoreColors.glassBorder(),
                shape = MaterialTheme.shapes.extraLarge
            ),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = Elevation.level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.l, vertical = Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(
                painter = painterResource(id = R.drawable.ic_lucide_bot),
                tint = MaterialTheme.colorScheme.primary,
                background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                size = 40.dp
            )

            Spacer(Modifier.size(Spacing.m))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mr. Robot Store",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI workspace • curated tools",
                    style = MaterialTheme.typography.labelMedium,
                    color = StoreColors.mutedText()
                )
            }

            StatusPill("Ready")
        }
    }
}

@Composable
private fun StoreWelcomeGreeting() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Good to see you,\ncommander.",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = "Your AI workspace is online. Browse featured tools, jump into chat, or spin up a new agent.",
            style = MaterialTheme.typography.bodyLarge,
            color = StoreColors.mutedText()
        )
    }
}

@Composable
private fun HeroBanner(onStartChat: () -> Unit) {
    val scheme = MaterialTheme.colorScheme

    ElevatedGlassCard(padding = Spacing.xxl, glow = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(scheme.primary, scheme.tertiary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lucide_sparkles),
                    contentDescription = null,
                    tint = scheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.size(Spacing.m))

            Text(
                text = "FEATURED",
                style = MaterialTheme.typography.labelSmall,
                color = scheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(Spacing.l))

        Text(
            text = "Start a conversation with Mr. Robot",
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(Spacing.s))

        Text(
            text = "Ask anything, attach files, dictate prompts, or run an agent on your code. The AI chat is tuned to your OpenRouter and provider models.",
            style = MaterialTheme.typography.bodyLarge,
            color = StoreColors.mutedText()
        )

        Spacer(Modifier.height(Spacing.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StoreButton(
                text = "Open AI chat",
                onClick = onStartChat,
                style = StoreButtonStyle.Filled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionGrid(
    actions: List<QuickAction>,
    onClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
        actions.chunked(2).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                rowActions.forEach { action ->
                    QuickActionTile(
                        action = action,
                        onClick = { onClick(action.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowActions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    GlassCard(
        modifier = modifier,
        onClick = onClick,
        padding = Spacing.l
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(
                painter = painterResource(id = action.iconRes),
                tint = scheme.primary,
                background = scheme.primaryContainer.copy(alpha = 0.40f),
                size = 40.dp
            )

            if (action.badge != null) {
                Spacer(modifier = Modifier.weight(1f))
                StatBadge(text = action.badge)
            }
        }

        Spacer(Modifier.height(Spacing.m))

        Text(
            text = action.title,
            style = MaterialTheme.typography.titleMedium,
            color = scheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )

        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = action.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = StoreColors.mutedText(),
            maxLines = 2
        )
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            PremiumMetric(
                label = "Screens",
                value = "20+",
                description = "Discovery, chat, agents, flows, logs"
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            PremiumMetric(
                label = "Themes",
                value = "5",
                description = "Ocean, Purple, AMOLED, Light"
            )
        }
    }
}

/**
 * Convenience overload for `StoreTile` that takes a drawable resource
 * directly so we don't have to import painterResource everywhere.
 */
@Composable
private fun StoreTile(
    title: String,
    description: String,
    painterRes: Int,
    tint: Color,
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        padding = Spacing.l
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(
                painter = painterResource(id = painterRes),
                tint = tint,
                background = tint.copy(alpha = 0.14f),
                size = 44.dp
            )

            Spacer(Modifier.size(Spacing.m))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (badge != null) {
                        Spacer(Modifier.size(Spacing.s))
                        StatBadge(text = badge, tint = tint)
                    }
                }

                Spacer(Modifier.height(Spacing.xs))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = StoreColors.mutedText(),
                    maxLines = 2
                )
            }
        }
    }
}
