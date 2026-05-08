package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun MarketplaceScreen() {
    val models = listOf("GPT-4o Mini", "Gemini Flash", "Claude Sonnet", "DeepSeek Chat")

    ScreenShell {
        Title("Marketplace")
        Subtitle("Models, tools, and agent skills.")
        Spacer(Modifier.height(16.dp))
        models.forEach {
            GlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Title(it)
                Subtitle("Available through OpenRouter model selection.")
            }
        }
    }
}
