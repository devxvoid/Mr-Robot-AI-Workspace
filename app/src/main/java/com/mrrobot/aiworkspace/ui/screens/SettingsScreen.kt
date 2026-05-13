package com.mrrobot.aiworkspace.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.data.AiModels
import com.mrrobot.aiworkspace.data.ApiProvider
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.PrimaryTonalButton
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.viewmodel.SettingsUiState
import com.mrrobot.aiworkspace.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    parentPadding: PaddingValues = PaddingValues()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var editorProvider by remember { mutableStateOf(ApiProvider.OpenRouter) }
    var editorApiKey by remember { mutableStateOf("") }
    var editorModel by remember {
        mutableStateOf(AiModels.defaultForProvider(ApiProvider.OpenRouter).id)
    }

    LaunchedEffect(state.isLoaded) {
        if (state.isLoaded) {
            val firstSaved = state.configuredProviderModels().firstOrNull()
            editorProvider = firstSaved?.provider ?: ApiProvider.OpenRouter
            editorApiKey = state.keyFor(editorProvider)
            editorModel = firstSaved?.modelId ?: AiModels.defaultForProvider(editorProvider).id
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Settings") },
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
                    title = "Configuration",
                    subtitle = "Configure models, API keys, themes, and workspace preferences."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                AppCard {
                    ProviderDashboard(
                        state = state,
                        onEditProvider = { provider ->
                            editorProvider = provider
                            editorApiKey = state.keyFor(provider)
                            editorModel = state.modelFor(provider)
                        },
                        onActivateProvider = viewModel::activateProvider
                    )

                    Spacer(Modifier.height(24.dp))

                    AddApiKeyCard(
                        provider = editorProvider,
                        apiKey = editorApiKey,
                        model = editorModel,
                        onProviderChange = { provider ->
                            editorProvider = provider
                            editorApiKey = state.keyFor(provider)
                            editorModel = state.modelFor(provider)
                        },
                        onApiKeyChange = { editorApiKey = it },
                        onModelChange = { editorModel = it },
                        onSave = {
                            viewModel.saveProviderConfiguration(
                                provider = editorProvider,
                                model = editorModel,
                                apiKey = editorApiKey,
                                activate = false
                            )
                        },
                        onSaveAndActivate = {
                            viewModel.saveProviderConfiguration(
                                provider = editorProvider,
                                model = editorModel,
                                apiKey = editorApiKey,
                                activate = true
                            )
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    ThemeSelectorCard(
                        selected = state.themeMode,
                        onSelected = viewModel::updateThemeMode
                    )

                    Spacer(Modifier.height(16.dp))

                    PrimaryTonalButton("Save Settings") { viewModel.save() }

                    if (state.savedMessage.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        CaptionText(state.savedMessage)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.clear() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Clear Settings")
                    }
                }
            }

            item {
                AppCard {
                    GroupTitle("Model Catalog")
                    Spacer(Modifier.height(4.dp))
                    BodyText("Choose a provider above to view its supported models.")
                    Spacer(Modifier.height(8.dp))

                    AiModels.byProvider(editorProvider).forEach { model ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { editorModel = model.id }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = model.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = model.id,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderDashboard(
    state: SettingsUiState,
    onEditProvider: (ApiProvider) -> Unit,
    onActivateProvider: (ApiProvider) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        GroupTitle("AI Provider")
        Spacer(Modifier.height(4.dp))
        BodyText("Configure models and runtime keys.")
        Spacer(Modifier.height(16.dp))

        ActiveProviderCard(state = state)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Your Keys",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        val saved = state.configuredProviderModels()

        if (saved.isEmpty()) {
            BodyText("No API keys saved yet. Add a key below.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                saved.forEach { config ->
                    SavedKeyRow(
                        provider = config.provider,
                        modelId = config.modelId,
                        isActive = state.hasActiveConfiguration() &&
                            config.provider == state.selectedProvider,
                        onEdit = { onEditProvider(config.provider) },
                        onActivate = { onActivateProvider(config.provider) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveProviderCard(state: SettingsUiState) {
    val hasActive = state.hasActiveConfiguration()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (hasActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            },
            contentColor = if (hasActive) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (hasActive) {
                        state.selectedProvider.displayName
                    } else {
                        "No active model"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (hasActive) {
                        AiModels.findById(state.modelFor(state.selectedProvider)).name
                    } else {
                        "Add a key below or activate a saved provider."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = if (hasActive) "Connected" else "No Key",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun SavedKeyRow(
    provider: ApiProvider,
    modelId: String,
    isActive: Boolean,
    onEdit: () -> Unit,
    onActivate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = if (isActive) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        contentColor = if (isActive) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = provider.displayName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${AiModels.findById(modelId).name} - ${if (isActive) "Active" else "Saved"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                androidx.compose.material3.TextButton(onClick = onEdit) { Text("Edit") }
                if (!isActive) {
                    androidx.compose.material3.TextButton(onClick = onActivate) { Text("Activate") }
                }
            }
        }
    }
}

@Composable
private fun AddApiKeyCard(
    provider: ApiProvider,
    apiKey: String,
    model: String,
    onProviderChange: (ApiProvider) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onSave: () -> Unit,
    onSaveAndActivate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add API Key",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(12.dp))

            ProviderDropdown(
                selected = provider,
                onSelected = onProviderChange
            )

            Spacer(Modifier.height(GroupSpacing))

            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                label = { Text(provider.keyLabel) },
                placeholder = { Text(provider.keyPlaceholder) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(GroupSpacing))

            ModelDropdown(
                selectedProvider = provider,
                selectedModel = model,
                onSelected = onModelChange
            )

            Spacer(Modifier.height(GroupSpacing))

            CaptionText(provider.helpText)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
            ) {
                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Save") }

                Button(
                    onClick = onSaveAndActivate,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Save & Activate", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderDropdown(
    selected: ApiProvider,
    onSelected: (ApiProvider) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Provider") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        androidx.compose.material3.ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ApiProvider.values().forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider.displayName) },
                    onClick = {
                        onSelected(provider)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelDropdown(
    selectedProvider: ApiProvider,
    selectedModel: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val models = AiModels.byProvider(selectedProvider)
    val selected = models.firstOrNull { it.id == selectedModel }
        ?: AiModels.defaultForProvider(selectedProvider)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Model") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        androidx.compose.material3.ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = model.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = model.id,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSelected(model.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeSelectorCard(
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        GroupTitle("Theme")
        Spacer(Modifier.height(4.dp))
        BodyText("Choose the app appearance and design mode.")
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            ThemeTile("Auto", AppThemeMode.Auto, selected, R.drawable.ic_lucide_sun_moon_exact, onSelected, Modifier.weight(1f))
            ThemeTile("Light", AppThemeMode.Light, selected, R.drawable.ic_lucide_sun, onSelected, Modifier.weight(1f))
            ThemeTile("Dark", AppThemeMode.Dark, selected, R.drawable.ic_lucide_moon, onSelected, Modifier.weight(1f))
        }

        Spacer(Modifier.height(GroupSpacing))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            ThemeTile("Cyber", AppThemeMode.Cyberpunk, selected, R.drawable.ic_lucide_cpu, onSelected, Modifier.weight(1f))
            ThemeTile("Hacker", AppThemeMode.Hacker, selected, R.drawable.ic_lucide_terminal_square, onSelected, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ThemeTile(
    title: String,
    mode: AppThemeMode,
    selected: AppThemeMode,
    @DrawableRes iconRes: Int,
    onSelected: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selected == mode

    Surface(
        modifier = modifier.clickable { onSelected(mode) },
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        contentColor = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}
