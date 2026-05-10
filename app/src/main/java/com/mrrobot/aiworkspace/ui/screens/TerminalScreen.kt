package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.mrrobot.aiworkspace.data.TerminalLine
import com.mrrobot.aiworkspace.data.TerminalLineType
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.TerminalViewModel
import kotlinx.coroutines.launch

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(state.logs.lastIndex)
            }
        }
    }

    ScreenShell {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title("Live Terminal")
                Subtitle("Simulated safe mobile command console and build logs.")
            }

            TextButton(onClick = { viewModel.clearLogs() }) {
                Text("Clear")
            }
        }

        Spacer(Modifier.height(12.dp))

        GlassCard {
            Subtitle("Quick Actions")
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.runQuickCommand("./gradlew assembleDebug") },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isRunning
                ) {
                    Text("Build")
                }

                OutlinedButton(
                    onClick = { viewModel.runQuickCommand("check openrouter") },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isRunning
                ) {
                    Text("AI")
                }

                OutlinedButton(
                    onClick = { viewModel.runQuickCommand("help") },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isRunning
                ) {
                    Text("Help")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF05070A)
            )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                items(
                    items = state.logs,
                    key = { it.id }
                ) { line ->
                    TerminalLineRow(line = line)
                }

                if (state.isRunning) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Running...",
                                color = NeonCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.input,
            onValueChange = viewModel::updateInput,
            placeholder = { Text("Type command, e.g. ./gradlew assembleDebug") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.runCurrentCommand() },
                modifier = Modifier.weight(1f),
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Run", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    val logs = state.logs.joinToString("\n") {
                        "[${it.timestamp}] ${it.type}: ${it.text}"
                    }
                    clipboard.setText(AnnotatedString(logs))
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Copy Logs")
            }
        }
    }
}

@Composable
private fun TerminalLineRow(line: TerminalLine) {
    val color = when (line.type) {
        TerminalLineType.Command -> NeonCyan
        TerminalLineType.Output -> Color(0xFFD1D5DB)
        TerminalLineType.Success -> Color(0xFF4ADE80)
        TerminalLineType.Warning -> Color(0xFFFACC15)
        TerminalLineType.Error -> Color(0xFFFF6B6B)
        TerminalLineType.Info -> NeonPurple
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = if (line.type == TerminalLineType.Command) {
                    Color(0xFF101827)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = "[${line.timestamp}] ",
            color = SoftText,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = line.text,
            color = color,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f)
        )
    }
}
