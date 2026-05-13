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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.TerminalLine
import com.mrrobot.aiworkspace.data.TerminalLineType
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.viewmodel.TerminalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = viewModel(),
    parentPadding: PaddingValues = PaddingValues()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(state.logs.lastIndex)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Live Terminal") },
                actions = {
                    TextButton(onClick = { viewModel.clearLogs() }) {
                        Text("Clear")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(mergedScreenPadding(innerPadding, parentPadding)),
            verticalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            SectionHeader(
                title = "Console",
                subtitle = "Safe mobile command console with simulated build logs."
            )

            AppCard {
                CaptionText("Quick Actions")
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.runQuickCommand("./gradlew assembleDebug") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        enabled = !state.isRunning
                    ) { Text("Build") }

                    OutlinedButton(
                        onClick = { viewModel.runQuickCommand("check openrouter") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        enabled = !state.isRunning
                    ) { Text("AI") }

                    OutlinedButton(
                        onClick = { viewModel.runQuickCommand("help") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        enabled = !state.isRunning
                    ) { Text("Help") }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(state.logs, key = { it.id }) { line ->
                        TerminalLineRow(line = line)
                    }

                    if (state.isRunning) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Running...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::updateInput,
                placeholder = { Text("Type command, e.g. ./gradlew assembleDebug") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.small
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
            ) {
                Button(
                    onClick = { viewModel.runCurrentCommand() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    enabled = !state.isRunning,
                    shape = MaterialTheme.shapes.large
                ) { Text("Run") }

                OutlinedButton(
                    onClick = {
                        val logs = state.logs.joinToString("\n") {
                            "[${it.timestamp}] ${it.type}: ${it.text}"
                        }
                        clipboard.setText(AnnotatedString(logs))
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Copy Logs") }
            }
        }
    }
}

@Composable
private fun TerminalLineRow(line: TerminalLine) {
    val scheme = MaterialTheme.colorScheme
    val lineColor = when (line.type) {
        TerminalLineType.Command -> scheme.primary
        TerminalLineType.Output -> scheme.onSurface
        TerminalLineType.Success -> scheme.tertiary
        TerminalLineType.Warning -> scheme.secondary
        TerminalLineType.Error -> scheme.error
        TerminalLineType.Info -> scheme.primary
    }

    val background = if (line.type == TerminalLineType.Command) {
        scheme.primaryContainer
    } else {
        scheme.surfaceContainerHighest
    }

    Surface(
        color = background,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "[${line.timestamp}] ",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = scheme.onSurfaceVariant
            )

            Text(
                text = line.text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = lineColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
