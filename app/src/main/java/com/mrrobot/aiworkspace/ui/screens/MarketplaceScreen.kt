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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.MarketplaceItem
import com.mrrobot.aiworkspace.data.MarketplaceItemType
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.MetricCard
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.ui.components.StatusChip
import com.mrrobot.aiworkspace.ui.components.TwoColumnRow
import com.mrrobot.aiworkspace.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    parentPadding: PaddingValues = PaddingValues()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Marketplace") },
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
                    title = "Catalog",
                    subtitle = "Enable models, tools, agents, workflows, and integrations."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                TwoColumnRow(
                    left = {
                        MetricCard(
                            label = "Enabled",
                            value = "${state.enabledCount}",
                            description = "Currently active modules"
                        )
                    },
                    right = {
                        MetricCard(
                            label = "Available",
                            value = "${state.items.size}",
                            description = "Total modules in catalog"
                        )
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::updateQuery,
                    placeholder = { Text("Search marketplace...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )
            }

            item {
                MarketplaceTypeFilters(
                    selectedType = state.selectedType,
                    onSelect = viewModel::selectType
                )
            }

            items(state.filteredItems, key = { it.id }) { marketItem ->
                MarketplaceItemCard(
                    item = marketItem,
                    onOpen = { viewModel.selectItem(marketItem) },
                    onToggle = { viewModel.toggleItem(marketItem.id) }
                )
            }

            if (state.filteredItems.isEmpty()) {
                item {
                    AppCard {
                        GroupTitle("No marketplace items found")
                        Spacer(Modifier.height(4.dp))
                        BodyText("Try another keyword or clear the selected type.")
                    }
                }
            }
        }

        state.selectedItem?.let { marketItem ->
            MarketplaceDetailsDialog(
                item = marketItem,
                onDismiss = { viewModel.closeDetails() },
                onToggle = { viewModel.toggleItem(marketItem.id) }
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
            label = { Text("All", style = MaterialTheme.typography.labelMedium) },
            shape = MaterialTheme.shapes.small
        )

        MarketplaceItemType.values().forEach { type ->
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
private fun MarketplaceItemCard(
    item: MarketplaceItem,
    onOpen: () -> Unit,
    onToggle: () -> Unit
) {
    AppCard(modifier = Modifier.clickable { onOpen() }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                GroupTitle("${marketplaceIcon(item.type)} ${item.title}")
                Spacer(Modifier.height(4.dp))
                CaptionText("${item.provider} - ${item.type.name}")
                Spacer(Modifier.height(6.dp))
                BodyText(item.description)
            }

            Switch(
                checked = item.enabled,
                onCheckedChange = { onToggle() }
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item.tags.take(3).forEach { tag ->
                StatusChip(tag)
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpen,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
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
            Text(
                text = "${marketplaceIcon(item.type)} ${item.title}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                CaptionText("${item.provider} - ${item.type.name}")
                Spacer(Modifier.height(8.dp))
                BodyText(item.description)
                Spacer(Modifier.height(12.dp))
                CaptionText("Tags: ${item.tags.joinToString()}")
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (item.enabled) "Status: Enabled" else "Status: Disabled",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (item.enabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onToggle) {
                Text(if (item.enabled) "Disable" else "Enable")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        shape = MaterialTheme.shapes.large
    )
}

private fun marketplaceIcon(type: MarketplaceItemType): String {
    return when (type) {
        MarketplaceItemType.Model -> "\uD83E\uDDE0"
        MarketplaceItemType.Agent -> "\uD83E\uDD16"
        MarketplaceItemType.Tool -> "\uD83D\uDEE0\uFE0F"
        MarketplaceItemType.Workflow -> "\uD83D\uDD01"
        MarketplaceItemType.Integration -> "\uD83D\uDD0C"
    }
}
