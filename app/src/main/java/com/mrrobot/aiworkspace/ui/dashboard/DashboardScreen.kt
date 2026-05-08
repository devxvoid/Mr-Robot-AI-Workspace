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

data class WorkspaceModule(
    val title: String,
    val description: String,
    val action: String
)

@Composable
fun DashboardScreen() {

    val modules = listOf(

        WorkspaceModule(
            "AI Chat",
            "OpenRouter streaming AI workspace.",
            "Launch"
        ),

        WorkspaceModule(
            "Agents",
            "Autonomous multi-agent orchestration.",
            "Open"
        ),

        WorkspaceModule(
            "Workflow",
            "Visual automation pipelines.",
            "Manage"
        ),

        WorkspaceModule(
            "Terminal",
            "Workspace shell and logs.",
            "Access"
        )
    )

    LazyColumn(

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
            .padding(18.dp),

        verticalArrangement =
            Arrangement.spacedBy(18.dp)

    ) {

        item {

            Text(
                text = "Mr. Robot",
                color = TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Elite AI Workspace Command Center",
                color = TextSecondary,
                fontSize = 15.sp
            )
        }

        item {

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
        }

        item {

            ActivityCard(
                title = "Workspace Runtime",
                status = "ONLINE",
                body =
                    "Core AI systems operational. Navigation, workflows, OpenRouter integration, and terminal services are active."
            )
        }

        item {

            TerminalPreviewCard()
        }

        item {

            Text(
                text = "Workspace Modules",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(modules.size) { index ->

            val item = modules[index]

            WorkspaceModuleCard(
                title = item.title,
                description = item.description,
                action = item.action,
                onClick = {}
            )
        }

        item {

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
