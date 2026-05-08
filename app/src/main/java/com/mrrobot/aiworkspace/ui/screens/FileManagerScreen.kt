package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.WorkspaceFile
import com.mrrobot.aiworkspace.data.WorkspaceFileType
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.FileManagerViewModel

@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current

    ScreenShell {
        Title("File Manager")
        Subtitle("Browse project files, design assets, and workflow outputs.")
        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::updateQuery,
            placeholder = { Text("Search files, paths, or descriptions...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        TypeFilters(
            selectedType = state.selectedType,
            onSelect = viewModel::selectType
        )

        Spacer(Modifier.height(10.dp))

        if (state.copiedMessage.isNotBlank()) {
            GlassCard {
                Subtitle(state.copiedMessage)
            }

            Spacer(Modifier.height(10.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(
                items = state.filteredFiles,
                key = { it.id }
            ) { file ->
                FileCard(
                    file = file,
                    onClick = { viewModel.selectFile(file) },
                    onCopyPath = {
                        clipboard.setText(AnnotatedString(file.path))
                        viewModel.markCopied("Path")
                    }
                )
            }

            if (state.filteredFiles.isEmpty()) {
                item {
                    GlassCard {
                        Title("No files found")
                        Subtitle("Try another search or clear filters.")
                    }
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }

        state.selectedFile?.let { file ->
            FilePreviewDialog(
                file = file,
                onDismiss = { viewModel.closePreview() },
                onCopyPath = {
                    clipboard.setText(AnnotatedString(file.path))
                    viewModel.markCopied("Path")
                },
                onCopyPreview = {
                    clipboard.setText(AnnotatedString(file.contentPreview))
                    viewModel.markCopied("Preview")
                }
            )
        }
    }
}

@Composable
private fun TypeFilters(
    selectedType: WorkspaceFileType?,
    onSelect: (WorkspaceFileType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == null,
            onClick = { onSelect(null) },
            label = { Text("All") }
        )

        WorkspaceFileType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onSelect(type) },
                label = { Text(type.name) }
            )
        }
    }
}

@Composable
private fun FileCard(
    file: WorkspaceFile,
    onClick: () -> Unit,
    onCopyPath: () -> Unit
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
                Text(
                    text = "${fileIcon(file.type)} ${file.name}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Subtitle(file.path)

                Spacer(Modifier.height(8.dp))

                Subtitle(file.description)
            }

            AssistChip(
                onClick = {},
                label = { Text(file.size) }
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Preview")
            }

            OutlinedButton(
                onClick = onCopyPath,
                modifier = Modifier.weight(1f)
            ) {
                Text("Copy Path")
            }
        }
    }
}

@Composable
private fun FilePreviewDialog(
    file: WorkspaceFile,
    onDismiss: () -> Unit,
    onCopyPath: () -> Unit,
    onCopyPreview: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${fileIcon(file.type)} ${file.name}")
        },
        text = {
            Column {
                Text(
                    text = file.path,
                    color = SoftText
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = file.description,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = file.contentPreview,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCopyPath) {
                Text("Copy Path")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onCopyPreview) {
                    Text("Copy Preview")
                }

                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

private fun fileIcon(type: WorkspaceFileType): String {
    return when (type) {
        WorkspaceFileType.Kotlin -> "🧩"
        WorkspaceFileType.Markdown -> "📝"
        WorkspaceFileType.Gradle -> "⚙️"
        WorkspaceFileType.Yaml -> "🔁"
        WorkspaceFileType.Zip -> "🗜️"
        WorkspaceFileType.Folder -> "📁"
        WorkspaceFileType.Text -> "📄"
        WorkspaceFileType.Unknown -> "❔"
    }
}
