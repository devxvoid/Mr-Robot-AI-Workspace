package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.MarketplaceItem
import com.mrrobot.aiworkspace.data.MarketplaceItemType
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.MarketplaceViewModel

@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ScreenShell {
        Title("Marketplace")
        Subtitle("Enable models, tools, agents, workflows, and integrations.")
        Spacer(Modifier.height(14.dp))

        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Title("${state.enabledCount}")
                    Subtitle("Enabled modules")
                }

                Column {
                    Title("${state.items.size}")
                    Subtitle("Available items")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::updateQuery,
            placeholder = { Text("Search marketplace...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        MarketplaceTypeFilters(
            selectedType = state.selectedType,
            onSelect = viewModel::selectType
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(
                items = state.filteredItems,
                key = { it.id }
            ) { item ->
                MarketplaceItemCard(
                    item = item,
                    onOpen = { viewModel.selectItem(item) },
                    onToggle = { viewModel.toggleItem(item.id) }
                )
            }

            if (state.filteredItems.isEmpty()) {
                item {
                    GlassCard {
                        Title("No marketplace items found")
                        Subtitle("Try another keyword or clear the selected type.")
                    }
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }

        state.selectedItem?.let { item ->
            MarketplaceDetailsDialog(
                item = item,
                onDismiss = { viewModel.closeDetails() },
                onToggle = { viewModel.toggleItem(item.id) }
            )
        }
    }
}

@Composable
private fun MarketplaceTypeFilters(
    selectedType: MarketplaceItemType?,
    onSelect: (MarketplaceItemType?) -> Unit
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

        MarketplaceItemType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onSelect(type) },
                label = { Text(type.name) }
            )
        }
    }
}

@Composable
private fun MarketplaceItemCard(
    item: MarketplaceItem,
    onOpen: () -> Unit,
    onToggle: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${marketplaceIcon(item.type)} ${item.title}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Subtitle("${item.provider} • ${item.type.name}")

                Spacer(Modifier.height(8.dp))

                Subtitle(item.description)
            }

            Switch(
                checked = item.enabled,
                onCheckedChange = { onToggle() }
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item.tags.take(3).forEach { tag ->
                AssistChip(
                    onClick = {},
                    label = { Text(tag) }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onOpen,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("View Details")
        }
    }
}

@Composable
private fun MarketplaceDetailsDialog(
    item: MarketplaceItem,
    onDismiss: () -> Unit,
    onToggle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${marketplaceIcon(item.type)} ${item.title}")
        },
        text = {
            Column {
                Text(
                    text = "${item.provider} • ${item.type.name}",
                    color = SoftText
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = item.description,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Tags: ${item.tags.joinToString()}",
                    color = SoftText
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (item.enabled) "Status: Enabled" else "Status: Disabled",
                    color = if (item.enabled) NeonCyan else SoftText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onToggle) {
                Text(if (item.enabled) "Disable" else "Enable")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun marketplaceIcon(type: MarketplaceItemType): String {
    return when (type) {
        MarketplaceItemType.Model -> "🧠"
        MarketplaceItemType.Agent -> "🤖"
        MarketplaceItemType.Tool -> "🛠️"
        MarketplaceItemType.Workflow -> "🔁"
        MarketplaceItemType.Integration -> "🔌"
    }
}
