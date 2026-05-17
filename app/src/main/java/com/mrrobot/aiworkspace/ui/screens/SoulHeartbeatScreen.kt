package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.HeartbeatConfig
import com.mrrobot.aiworkspace.data.HeartbeatLogEntry
import com.mrrobot.aiworkspace.data.SoulConfig
import com.mrrobot.aiworkspace.ui.components.PremiumHeader
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.viewmodel.SoulHeartbeatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulHeartbeatScreen(
    viewModel: SoulHeartbeatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage.isNotBlank()) {
            snackbar.showSnackbar(state.savedMessage)
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenShell {
            PremiumHeader(
                title = "Soul & Heartbeat",
                subtitle = "Customize the AI's persistent persona and configure autonomous self-checks."
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                item { SoulSection(state, viewModel) }
                item { HeartbeatSection(state, viewModel) }
                if (state.log.isNotEmpty()) {
                    item { HeartbeatLogSection(state.log, viewModel::clearLog) }
                }
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}

@Composable
private fun SoulSection(
    state: com.mrrobot.aiworkspace.viewmodel.SoulHeartbeatUiState,
    viewModel: SoulHeartbeatViewModel
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👻", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Soul (System Prompt)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "The base persona prepended to every chat. Memories are appended below this.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.soulPrompt,
                onValueChange = viewModel::updateSoul,
                placeholder = { Text(SoulConfig.DEFAULT_SOUL_PROMPT) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 12,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = viewModel::saveSoul,
                    enabled = state.isSoulDirty,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Soul")
                }
                OutlinedButton(
                    onClick = viewModel::resetSoul,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
private fun HeartbeatSection(
    state: com.mrrobot.aiworkspace.viewmodel.SoulHeartbeatUiState,
    viewModel: SoulHeartbeatViewModel
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💓", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Heartbeat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.heartbeatEnabled,
                    onCheckedChange = viewModel::toggleHeartbeat
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Periodic self-checks that let the AI review memories and pending tasks without you typing. " +
                    "Runs every ${state.intervalMinutes} min between ${state.activeHoursStart}:00 and ${state.activeHoursEnd}:00.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(14.dp))

            // Interval slider
            Text(
                text = "Interval: ${state.intervalMinutes} min",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = state.intervalMinutes.toFloat(),
                onValueChange = { viewModel.setInterval(it.toInt()) },
                valueRange = 5f..240f,
                steps = ((240 - 5) / 5) - 1,
                enabled = state.heartbeatEnabled
            )

            Spacer(Modifier.height(8.dp))

            // Active hours
            Text(
                text = "Active hours: ${state.activeHoursStart}:00 → ${state.activeHoursEnd}:00",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = state.activeHoursStart.toFloat(),
                        onValueChange = { viewModel.setActiveStart(it.toInt()) },
                        valueRange = 0f..23f,
                        steps = 22,
                        enabled = state.heartbeatEnabled
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = state.activeHoursEnd.toFloat(),
                        onValueChange = { viewModel.setActiveEnd(it.toInt()) },
                        valueRange = 1f..24f,
                        steps = 22,
                        enabled = state.heartbeatEnabled
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Heartbeat Prompt",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = state.heartbeatPrompt,
                onValueChange = viewModel::updateHeartbeatPrompt,
                placeholder = { Text(HeartbeatConfig.DEFAULT_HEARTBEAT_PROMPT) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 8,
                enabled = state.heartbeatEnabled,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = viewModel::saveHeartbeat,
                    enabled = state.isHeartbeatDirty,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                OutlinedButton(
                    onClick = viewModel::runHeartbeatNow,
                    enabled = !state.isRunning,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isRunning) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Running…")
                    } else {
                        Text("Run Now")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeartbeatLogSection(
    log: List<HeartbeatLogEntry>,
    onClear: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Recent Activity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onClear) { Text("Clear", fontSize = 12.sp) }
            }

            Spacer(Modifier.height(8.dp))

            log.take(10).forEach { entry ->
                LogRow(entry)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LogRow(entry: HeartbeatLogEntry) {
    val time = remember(entry.timestampEpochMs) {
        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(entry.timestampEpochMs))
    }
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = if (entry.success) "✅" else "⚠️",
            fontSize = 14.sp
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = time,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            val body = entry.response ?: entry.error ?: "(empty)"
            Text(
                text = body.take(160),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}
