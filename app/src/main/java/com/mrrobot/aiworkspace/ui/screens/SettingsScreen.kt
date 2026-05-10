package com.mrrobot.aiworkspace.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.AiModels
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.ui.components.CyberButton
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title
import com.mrrobot.aiworkspace.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val selectedModel = AiModels.findById(state.model)

    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item {
                Title("Settings")
                Subtitle("Configure OpenRouter, model selection, Cyber/Hacker design themes, and workspace preferences.")

                Spacer(Modifier.height(14.dp))

                GlassCard {
                    Subtitle("OpenRouter API Key")

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.apiKey,
                        onValueChange = viewModel::updateApiKey,
                        placeholder = { Text("sk-or-v1-...") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))

                    Subtitle("Theme")

                    Spacer(Modifier.height(8.dp))

                    ThemeModeSelector(
                        selected = state.themeMode,
                        onSelected = viewModel::updateThemeMode
                    )

                    Spacer(Modifier.height(18.dp))

                    Subtitle("Selected Model")

                    Spacer(Modifier.height(8.dp))

                    ModelSelector(
                        selectedModelId = state.model,
                        onModelSelected = viewModel::updateModel
                    )

                    Spacer(Modifier.height(14.dp))

                    GlassCard {
                        Title(selectedModel.name)
                        Subtitle("${selectedModel.provider} • ${selectedModel.id}")

                        Spacer(Modifier.height(6.dp))

                        Subtitle(selectedModel.description)
                    }

                    Spacer(Modifier.height(16.dp))

                    CyberButton("Save Settings") {
                        viewModel.save()
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.clear() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Clear Settings")
                    }

                    if (state.savedMessage.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Subtitle(state.savedMessage)
                    }
                }

                Spacer(Modifier.height(16.dp))

                GlassCard {
                    Title("Theme Guide")

                    Spacer(Modifier.height(8.dp))

                    Subtitle("Auto follows your Android system theme.")
                    Subtitle("Dark keeps the existing dark UI.")
                    Subtitle("Light keeps the clean bright UI.")
                    Subtitle("Cyber uses the imported Mr. Robot Cyberpunk System style.")
                    Subtitle("Hacker uses the imported red/black Hacker Mode style.")

                    Spacer(Modifier.height(12.dp))

                    HorizontalDivider()

                    Spacer(Modifier.height(12.dp))

                    Title("Model Catalog")

                    Spacer(Modifier.height(8.dp))

                    AiModels.supported.forEach { model ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateModel(model.id)
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Subtitle("${model.name} — ${model.provider}")
                            Subtitle(model.id)
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeModeSelector(
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeChip(
                mode = AppThemeMode.Auto,
                label = "Auto",
                selected = selected,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )

            ThemeChip(
                mode = AppThemeMode.Dark,
                label = "Dark",
                selected = selected,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )

            ThemeChip(
                mode = AppThemeMode.Light,
                label = "Light",
                selected = selected,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeChip(
                mode = AppThemeMode.Cyberpunk,
                label = "Cyber",
                selected = selected,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )

            ThemeChip(
                mode = AppThemeMode.Hacker,
                label = "Hacker",
                selected = selected,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeChip(
    mode: AppThemeMode,
    label: String,
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected == mode,
        onClick = { onSelected(mode) },
        label = { Text(label) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelSelector(
    selectedModelId: String,
    onModelSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = AiModels.findById(selectedModelId)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = "${selected.name} • ${selected.provider}",
            onValueChange = {},
            readOnly = true,
            label = { Text("AI Model") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AiModels.supported.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(model.name)
                            Text(model.provider)
                        }
                    },
                    onClick = {
                        onModelSelected(model.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
