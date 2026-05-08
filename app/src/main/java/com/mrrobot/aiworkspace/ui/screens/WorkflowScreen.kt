package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.WorkflowStatus
import com.mrrobot.aiworkspace.data.WorkflowStep
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.WorkflowViewModel

@Composable
fun WorkflowScreen(viewModel: WorkflowViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current

    ScreenShell {
        Title("Workflow Builder")
        Subtitle("Create, reorder, simulate, and export multi-agent workflows.")
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item {
                GlassCard {
                    Subtitle("Workflow Name")
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.workflowName,
                        onValueChange = viewModel::updateWorkflowName,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.loadAndroidTemplate() },
                                modifier = Modifier.weight(1f)
                            ) { Text("Android") }

                            OutlinedButton(
                                onClick = { viewModel.loadAiWorkspaceTemplate() },
                                modifier = Modifier.weight(1f)
                            ) { Text("AI Upgrade") }
                        }

                        OutlinedButton(
                            onClick = { viewModel.clearSteps() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Clear Steps") }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(
                items = state.steps,
                key = { _, step -> step.id }
            ) { index, step ->
                WorkflowStepCard(
                    index = index,
                    step = step,
                    onUp = { viewModel.moveStepUp(step.id) },
                    onDown = { viewModel.moveStepDown(step.id) },
                    onRun = { viewModel.markStepRunning(step.id) },
                    onDone = { viewModel.markStepCompleted(step.id) },
                    onFail = { viewModel.markStepFailed(step.id) },
                    onRemove = { viewModel.removeStep(step.id) }
                )
            }

            item {
                AddStepCard(
                    title = state.customTitle,
                    description = state.customDescription,
                    agent = state.customAgent,
                    onTitleChange = viewModel::updateCustomTitle,
                    onDescriptionChange = viewModel::updateCustomDescription,
                    onAgentChange = viewModel::updateCustomAgent,
                    onAdd = { viewModel.addCustomStep() }
                )

                Spacer(Modifier.height(12.dp))

                GlassCard {
                    CyberButton("Generate Workflow Prompt") {
                        viewModel.generatePrompt()
                    }

                    if (state.generatedPrompt.isNotBlank()) {
                        Spacer(Modifier.height(14.dp))
                        Subtitle("Generated Prompt")
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = state.generatedPrompt,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(Modifier.height(12.dp))

                        CyberButton("Copy Workflow Prompt") {
                            clipboard.setText(AnnotatedString(state.generatedPrompt))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkflowStepCard(
    index: Int,
    step: WorkflowStep,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onRun: () -> Unit,
    onDone: () -> Unit,
    onFail: () -> Unit,
    onRemove: () -> Unit
) {
    GlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title("${index + 1}. ${step.title}")
                Subtitle(step.agent)
                Spacer(Modifier.height(8.dp))
                Subtitle(step.description)
                Spacer(Modifier.height(8.dp))
                StatusChip(step.status)
            }

            TextButton(onClick = onRemove) {
                Text("Remove")
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedButton(onClick = onUp, modifier = Modifier.weight(1f)) { Text("Up") }
            OutlinedButton(onClick = onDown, modifier = Modifier.weight(1f)) { Text("Down") }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedButton(onClick = onRun, modifier = Modifier.weight(1f)) { Text("Run") }
            OutlinedButton(onClick = onDone, modifier = Modifier.weight(1f)) { Text("Done") }
            OutlinedButton(onClick = onFail, modifier = Modifier.weight(1f)) { Text("Fail") }
        }
    }
}

@Composable
private fun StatusChip(status: WorkflowStatus) {
    val color = when (status) {
        WorkflowStatus.Pending -> MaterialTheme.colorScheme.onSurfaceVariant
        WorkflowStatus.Running -> MaterialTheme.colorScheme.primary
        WorkflowStatus.Completed -> Color(0xFF22C55E)
        WorkflowStatus.Failed -> Color(0xFFFF6B6B)
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = status.name,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
private fun AddStepCard(
    title: String,
    description: String,
    agent: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAgentChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    GlassCard {
        Title("Add Custom Step")
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Step title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Step description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = agent,
            onValueChange = onAgentChange,
            placeholder = { Text("Assigned agent") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        CyberButton("Add Step") {
            onAdd()
        }
    }
}
