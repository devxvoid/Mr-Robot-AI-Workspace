package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.MetricCard
import com.mrrobot.aiworkspace.ui.components.PrimaryTonalButton
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.ui.components.StatusChip
import com.mrrobot.aiworkspace.ui.components.TwoColumnRow

/**
 * Home / Welcome screen.
 *
 * Uses a [MediumTopAppBar] with exitUntilCollapsed scroll behavior so the
 * header compresses as the user scrolls - the canonical M3 pattern for
 * a brand landing screen. All cards are [AppCard]s (ElevatedCard under
 * the hood) with zero borders and tonal elevation only.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    nav: NavController,
    parentPadding: PaddingValues = PaddingValues()
) {
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Mr. Robot AI") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = mergedScreenPadding(innerPadding, parentPadding),
            verticalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            item {
                SectionHeader(
                    title = "Workspace",
                    subtitle = "A calm, powerful Android workspace for chat, agents, " +
                        "workflows, terminal logs, marketplace tools, and AI models."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                AppCard {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
                    ) {
                        StatusChip("Chat")
                        StatusChip("Agents")
                        StatusChip("Workflows")
                    }

                    Spacer(Modifier.height(16.dp))

                    GroupTitle("What can I help you with today?")

                    Spacer(Modifier.height(4.dp))

                    BodyText(
                        "Start a conversation, launch an agent, or build an automation " +
                            "from a calmer AI workspace."
                    )

                    Spacer(Modifier.height(16.dp))

                    PrimaryTonalButton("Start New Chat") {
                        nav.navigate(Route.Chat.path)
                    }

                    Spacer(Modifier.height(GroupSpacing))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Agents.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Open Agents")
                    }

                    Spacer(Modifier.height(GroupSpacing))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Workflow.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Build Workflow")
                    }
                }
            }

            item {
                TwoColumnRow(
                    left = {
                        MetricCard(
                            label = "Workspace",
                            value = "20+",
                            description = "Screens and tools"
                        )
                    },
                    right = {
                        MetricCard(
                            label = "Themes",
                            value = "5",
                            description = "Set in Settings"
                        )
                    }
                )
            }

            item {
                TwoColumnRow(
                    left = {
                        MetricCard(
                            label = "Model",
                            value = "AI",
                            description = "Multi-provider ready"
                        )
                    },
                    right = {
                        MetricCard(
                            label = "Agents",
                            value = "5",
                            description = "Role-based system"
                        )
                    }
                )
            }
        }
    }
}
