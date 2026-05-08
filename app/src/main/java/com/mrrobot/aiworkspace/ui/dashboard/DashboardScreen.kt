package com.mrrobot.aiworkspace.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.ui.theme.*

data class WorkspaceCard(
    val title: String,
    val description: String,
    val action: String
)

@Composable
fun DashboardScreen() {

    val modules = listOf(

        WorkspaceCard(
            "AI Chat",
            "Streaming OpenRouter workspace with multi-model support.",
            "Launch"
        ),

        WorkspaceCard(
            "Agents",
            "Multi-agent orchestration and prompt execution.",
            "Open"
        ),

        WorkspaceCard(
            "Workflow",
            "Visual pipeline builder and execution engine.",
            "Manage"
        ),

        WorkspaceCard(
            "Terminal",
            "Workspace shell, logs, and runtime diagnostics.",
            "Access"
        ),

        WorkspaceCard(
            "Marketplace",
            "Models, plugins, integrations, and tools.",
            "Explore"
        ),

        WorkspaceCard(
            "Files",
            "Workspace exports and generated assets.",
            "Browse"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        WorkspaceBg,
                        WorkspaceBgSecondary,
                        WorkspaceBg
                    )
                )
            )
            .padding(18.dp)
    ) {

        Text(
            text = "Mr. Robot",
            color = TextPrimary,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Elite AI Workspace Command Center",
            color = TextSecondary,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            PremiumStatCard(
                title = "Agents",
                value = "12",
                modifier = Modifier.weight(1f)
            )

            PremiumStatCard(
                title = "Models",
                value = "8",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        PremiumStatCard(
            title = "Workspace Status",
            value = "ONLINE"
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            items(modules.size) { index ->

                val item = modules[index]

                WorkspaceModuleCard(
                    title = item.title,
                    description = item.description,
                    action = item.action,
                    onClick = {}
                )
            }
        }
    }
}
