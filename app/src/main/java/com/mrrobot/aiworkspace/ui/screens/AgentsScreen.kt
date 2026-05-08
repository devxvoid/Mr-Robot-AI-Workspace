package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.Agent
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.AgentsViewModel

@Composable
fun AgentsScreen(
    viewModel: AgentsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current

    ScreenShell {
        Title("Agents")
        Subtitle("Build specialized prompts and execution roles for your AI workspace.")
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(state.agents) { agent ->
                AgentCard(
                    agent = agent,
                    selected = agent.id == state.selectedAgent.id,
                    onClick = { viewModel.selectAgent(agent) }
                )
            }
        }

        GlassCard {
            Title(state.selectedAgent.name)
            Subtitle("${state.selectedAgent.role} • ${state.selectedAgent.status}")
            Spacer(Modifier.height(8.dp))
            Subtitle(state.selectedAgent.description)

            Spacer(Modifier.height(14.dp))

            Subtitle("Task for this agent")

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.taskInput,
                onValueChange = viewModel::updateTask,
                placeholder = { Text("Example: Build the OpenRouter streaming chat screen...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 5
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.generatePrompt() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Generate Prompt")
                }

                OutlinedButton(
                    onClick = { viewModel.clear() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }

            if (state.generatedPrompt.isNotBlank()) {
                Spacer(Modifier.height(16.dp))

                Subtitle("Generated Agent Prompt")

                Spacer(Modifier.height(8.dp))

                Text(
                    text = state.generatedPrompt,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(Modifier.height(12.dp))

                CyberButton("Copy Prompt") {
                    clipboard.setText(AnnotatedString(state.generatedPrompt))
                }
            }
        }

        Spacer(Modifier.height(70.dp))
    }
}

@Composable
private fun AgentCard(
    agent: Agent,
    selected: Boolean,
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
                Title(agent.name)
                Subtitle(agent.role)
                Spacer(Modifier.height(6.dp))
                Subtitle(agent.description)
            }

            Text(
                text = if (selected) "ACTIVE" else agent.status,
                color = if (selected) NeonCyan else SoftText
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            agent.skills.take(3).forEach { skill ->
                AssistChip(
                    onClick = {},
                    label = { Text(skill) }
                )
            }
        }
    }
}
