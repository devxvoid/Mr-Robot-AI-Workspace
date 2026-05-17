package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.MemoryCategory
import com.mrrobot.aiworkspace.data.MemoryEntry
import com.mrrobot.aiworkspace.ui.components.PremiumHeader
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.StatusPill
import com.mrrobot.aiworkspace.viewmodel.MemoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    viewModel: MemoryViewModel = viewModel()
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
                title = "Memories",
                subtitle = "Persistent facts, preferences and learnings the AI remembers across conversations."
            )

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MemoryStat(
                    label = "Stored",
                    value = state.memories.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                MemoryStat(
                    label = "Promotion ready",
                    value = state.promotionCandidates.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(14.dp))

            if (state.promotionCandidates.isNotEmpty()) {
                Text(
                    text = "PROMOTION CANDIDATES",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(Modifier.height(8.dp))
                state.promotionCandidates.take(3).forEach { mem ->
                    PromoteCandidateRow(
                        memory = mem,
                        onPromote = { viewModel.promoteToSoul(mem) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(14.dp))
            }

            Text(
                text = "ALL MEMORIES",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(10.dp))

            if (state.memories.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(state.memories, key = { it.key }) { memory ->
                        MemoryCard(
                            memory = memory,
                            onEdit = { viewModel.showEdit(memory) },
                            onForget = { viewModel.forget(memory) },
                            onReinforce = { viewModel.reinforce(memory) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.showCreate() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add memory")
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }

    if (state.showEditor) {
        MemoryEditorSheet(
            isEditing = state.editingKey != null,
            keyValue = state.editorKey,
            content = state.editorContent,
            category = state.editorCategory,
            onKeyChange = viewModel::updateEditorKey,
            onContentChange = viewModel::updateEditorContent,
            onCategoryChange = viewModel::updateEditorCategory,
            onSave = { viewModel.save() },
            onDismiss = { viewModel.dismissEditor() }
        )
    }
}

@Composable
private fun MemoryStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PromoteCandidateRow(
    memory: MemoryEntry,
    onPromote: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = memory.key,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Reinforced ${memory.hitCount}× — promote into soul?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            TextButton(onClick = onPromote) {
                Text("Promote")
            }
        }
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryEntry,
    onEdit: () -> Unit,
    onForget: () -> Unit,
    onReinforce: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(
                    text = memory.category.name,
                    color = categoryColor(memory.category)
                )
                Spacer(Modifier.weight(1f))
                if (memory.hitCount > 1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${memory.hitCount}×",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = memory.key,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = memory.content,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onReinforce,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Reinforce", fontSize = 12.sp)
                }
                OutlinedIconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                }
                OutlinedIconButton(
                    onClick = onForget,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Forget",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun categoryColor(category: MemoryCategory): Color = when (category) {
    MemoryCategory.GENERAL -> MaterialTheme.colorScheme.primary
    MemoryCategory.LEARNING -> MaterialTheme.colorScheme.tertiary
    MemoryCategory.PREFERENCE -> MaterialTheme.colorScheme.secondary
    MemoryCategory.ERROR -> MaterialTheme.colorScheme.error
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🧠",
                fontSize = 48.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No memories stored yet",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap + to teach the AI a fact, preference or learning.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoryEditorSheet(
    isEditing: Boolean,
    keyValue: String,
    content: String,
    category: MemoryCategory,
    onKeyChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onCategoryChange: (MemoryCategory) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Edit Memory" else "New Memory",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = keyValue,
                onValueChange = onKeyChange,
                label = { Text("Key") },
                placeholder = { Text("e.g. user_timezone") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isEditing
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                label = { Text("Content") },
                placeholder = { Text("e.g. America/Los_Angeles") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 5
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "CATEGORY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MemoryCategory.values().forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat.name, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isEditing) "Save" else "Create", fontWeight = FontWeight.Bold)
            }
        }
    }
}
