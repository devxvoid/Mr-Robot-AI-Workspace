package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.mrrobot.aiworkspace.data.WorkflowStatus
import com.mrrobot.aiworkspace.data.WorkflowStep
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.PrimaryTonalButton
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.ui.components.StatusChip
import com.mrrobot.aiworkspace.viewmodel.WorkflowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowScreen(
    viewModel: WorkflowViewModel = viewModel(),
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
                title = { Text("Workflow Builder") },
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
                    title = "Orchestrate",
                    subtitle = "Create, reorder, simulate, and export multi-agent workflows."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                AppCard {
                    CaptionText("Workflow Name")
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.workflowName,
                        onValueChange = viewModel::updateWorkflowName,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(GroupSpacing)) {
                        OutlinedButton(
                            onClick = { viewModel.loadAndroidTemplate() },
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) { Text("Android") }

                        OutlinedButton(
                            onClick = { viewModel.loadAiWorkspaceTemplate() },
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) { Text("AI Upgrade") }
                    }

                    Spacer(Modifier.height(GroupSpacing))

                    OutlinedButton(
                        onClick = { viewModel.clearSteps() },
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) { Text("Clear Steps") }
                }
            }

            itemsIndexed(state.steps, key = { _, step -> step.id }) { index, step ->
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
            }

            item {
                AppCard {
                    PrimaryTonalButton("Generate Workflow Prompt") {
                        viewModel.generatePrompt()
                    }

                    if (state.generatedPrompt.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        CaptionText("Generated Prompt")
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
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        PrimaryTonalButton("Copy Workflow Prompt") {
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
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                GroupTitle("${index + 1}. ${step.title}")
                Spacer(Modifier.height(4.dp))
                CaptionText(step.agent)
                Spacer(Modifier.height(8.dp))
                BodyText(step.description)
                Spacer(Modifier.height(8.dp))
                StatusChip(step.status.name)
            }

            TextButton(onClick = onRemove) { Text("Remove") }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            OutlinedButton(
                onClick = onUp,
                modifier = Modifier.weight(1f).height(40.dp)
            ) { Text("Up") }
            OutlinedButton(
                onClick = onDown,
                modifier = Modifier.weight(1f).height(40.dp)
            ) { Text("Down") }
        }

        Spacer(Modifier.height(GroupSpacing))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            OutlinedButton(
                onClick = onRun,
                modifier = Modifier.weight(1f).height(40.dp)
            ) { Text("Run") }
            OutlinedButton(
                onClick = onDone,
                modifier = Modifier.weight(1f).height(40.dp)
            ) { Text("Done") }
            OutlinedButton(
                onClick = onFail,
                modifier = Modifier.weight(1f).height(40.dp)
            ) { Text("Fail") }
        }
    }
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
    AppCard {
        GroupTitle("Add Custom Step")
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Step title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )

        Spacer(Modifier.height(GroupSpacing))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Step description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            shape = MaterialTheme.shapes.small
        )

        Spacer(Modifier.height(GroupSpacing))

        OutlinedTextField(
            value = agent,
            onValueChange = onAgentChange,
            placeholder = { Text("Assigned agent") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )

        Spacer(Modifier.height(16.dp))

        PrimaryTonalButton("Add Step") { onAdd() }
    }
}

// Status mapping preserved for compile compatibility; StatusChip shows the
// WorkflowStatus.name and the card is colored purely by M3 tokens.
@Suppress("unused")
private fun workflowStatusLabel(status: WorkflowStatus): String = status.name
