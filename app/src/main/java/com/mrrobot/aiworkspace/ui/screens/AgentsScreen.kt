package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.Agent
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.PrimaryTonalButton
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.viewmodel.AgentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen(
    viewModel: AgentsViewModel = viewModel(),
    parentPadding: PaddingValues = PaddingValues()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Agents") },
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
                    title = "Specialized agents",
                    subtitle = "Select a role-based agent, describe the task, and generate a prompt."
                )
                Spacer(Modifier.height(8.dp))
            }

            items(state.agents, key = { it.id }) { agent ->
                AgentCard(
                    agent = agent,
                    selected = agent.id == state.selectedAgent.id,
                    onClick = { viewModel.selectAgent(agent) }
                )
            }

            item {
                AppCard {
                    GroupTitle(state.selectedAgent.name)
                    Spacer(Modifier.height(4.dp))
                    CaptionText("${state.selectedAgent.role} - ${state.selectedAgent.status}")
                    Spacer(Modifier.height(8.dp))
                    BodyText(state.selectedAgent.description)

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Task for this agent",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.taskInput,
                        onValueChange = viewModel::updateTask,
                        placeholder = { Text("Example: Build the chat screen streaming flow...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5,
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
                    ) {
                        Button(
                            onClick = { viewModel.generatePrompt() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = MaterialTheme.shapes.large
                        ) { Text("Generate Prompt") }

                        OutlinedButton(
                            onClick = { viewModel.clear() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) { Text("Clear") }
                    }

                    if (state.generatedPrompt.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Generated prompt",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = state.generatedPrompt,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        PrimaryTonalButton("Copy Prompt") {
                            clipboard.setText(AnnotatedString(state.generatedPrompt))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgentCard(
    agent: Agent,
    selected: Boolean,
    onClick: () -> Unit
) {
    AppCard(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                GroupTitle(agent.name)
                Spacer(Modifier.height(2.dp))
                CaptionText(agent.role)
                Spacer(Modifier.height(6.dp))
                BodyText(agent.description)
            }

            Text(
                text = if (selected) "ACTIVE" else agent.status.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            agent.skills.take(3).forEach { skill ->
                com.mrrobot.aiworkspace.ui.components.StatusChip(skill)
            }
        }
    }
}
