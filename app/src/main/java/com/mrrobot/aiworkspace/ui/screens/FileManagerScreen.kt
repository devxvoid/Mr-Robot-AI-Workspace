package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.mrrobot.aiworkspace.data.WorkspaceFile
import com.mrrobot.aiworkspace.data.WorkspaceFileType
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.ui.components.StatusChip
import com.mrrobot.aiworkspace.viewmodel.FileManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel = viewModel(),
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
                title = { Text("File Manager") },
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
                    title = "Browse",
                    subtitle = "Project files, design assets, and workflow outputs."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::updateQuery,
                    placeholder = { Text("Search files, paths, or descriptions...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )
            }

            item {
                TypeFilters(
                    selectedType = state.selectedType,
                    onSelect = viewModel::selectType
                )
            }

            if (state.copiedMessage.isNotBlank()) {
                item {
                    AppCard {
                        BodyText(state.copiedMessage)
                    }
                }
            }

            items(state.filteredFiles, key = { it.id }) { file ->
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
                    AppCard {
                        GroupTitle("No files found")
                        Spacer(Modifier.height(4.dp))
                        BodyText("Try another search or clear filters.")
                    }
                }
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
            label = { Text("All", style = MaterialTheme.typography.labelMedium) },
            shape = MaterialTheme.shapes.small
        )

        WorkspaceFileType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onSelect(type) },
                label = { Text(type.name, style = MaterialTheme.typography.labelMedium) },
                shape = MaterialTheme.shapes.small
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
    AppCard(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                GroupTitle("${fileIcon(file.type)} ${file.name}")
                Spacer(Modifier.height(4.dp))
                CaptionText(file.path)
                Spacer(Modifier.height(6.dp))
                BodyText(file.description)
            }

            StatusChip(file.size)
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text("Preview")
            }

            OutlinedButton(
                onClick = onCopyPath,
                modifier = Modifier.weight(1f).height(44.dp)
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
            Text(
                text = "${fileIcon(file.type)} ${file.name}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                CaptionText(file.path)
                Spacer(Modifier.height(8.dp))
                BodyText(file.description)
                Spacer(Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = file.contentPreview,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCopyPath) { Text("Copy Path") }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onCopyPreview) { Text("Copy Preview") }
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        },
        shape = MaterialTheme.shapes.large
    )
}

private fun fileIcon(type: WorkspaceFileType): String {
    return when (type) {
        WorkspaceFileType.Kotlin -> "\uD83E\uDDE9"
        WorkspaceFileType.Markdown -> "\uD83D\uDCDD"
        WorkspaceFileType.Gradle -> "\u2699\uFE0F"
        WorkspaceFileType.Yaml -> "\uD83D\uDD01"
        WorkspaceFileType.Zip -> "\uD83D\uDDDC\uFE0F"
        WorkspaceFileType.Folder -> "\uD83D\uDCC1"
        WorkspaceFileType.Text -> "\uD83D\uDCC4"
        WorkspaceFileType.Unknown -> "\u2754"
    }
}
