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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrrobot.aiworkspace.ui.components.GlassCard

data class WorkspaceModule(
    val title: String,
    val description: String
)

@Composable
fun DashboardScreen() {

    val modules = listOf(
        WorkspaceModule(
            "AI Chat",
            "OpenRouter streaming workspace"
        ),
        WorkspaceModule(
            "Agents",
            "Multi-agent orchestration system"
        ),
        WorkspaceModule(
            "Workflow",
            "Visual automation pipeline"
        ),
        WorkspaceModule(
            "Terminal",
            "Build logs and shell execution"
        ),
        WorkspaceModule(
            "Marketplace",
            "Models and integrations"
        ),
        WorkspaceModule(
            "Files",
            "Workspace assets and exports"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020617),
                        Color(0xFF071427),
                        Color(0xFF020617)
                    )
                )
            )
            .padding(18.dp)
    ) {

        Text(
            text = "Mr. Robot Workspace",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Production-grade AI command center.",
            color = Color(0xFF94A3B8),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        GlassCard {

            Text(
                text = "Workspace Status",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "All core systems operational.",
                color = Color(0xFFCBD5E1)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxHeight()
        ) {

            items(modules) { module ->

                GlassCard {

                    Text(
                        text = module.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = module.description,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
