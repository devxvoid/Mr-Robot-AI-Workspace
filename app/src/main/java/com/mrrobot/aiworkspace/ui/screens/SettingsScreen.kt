package com.mrrobot.aiworkspace.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
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
                Text(
                    text = "Settings",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 34.sp
                )

                Spacer(Modifier.height(8.dp))

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

                    ThemeSelectorCard(
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
                    Subtitle("Light keeps a clean bright interface.")
                    Subtitle("Dark keeps the premium dark interface.")
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
private fun ThemeSelectorCard(
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Title("Theme")

        Spacer(Modifier.height(6.dp))

        Subtitle("Choose the app appearance and design mode.")

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemeTile(
                title = "Auto",
                mode = AppThemeMode.Auto,
                selected = selected,
                iconRes = R.drawable.ic_lucide_sun_moon,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )

            ThemeTile(
                title = "Light",
                mode = AppThemeMode.Light,
                selected = selected,
                iconRes = R.drawable.ic_lucide_sun,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )

            ThemeTile(
                title = "Dark",
                mode = AppThemeMode.Dark,
                selected = selected,
                iconRes = R.drawable.ic_lucide_moon,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemeTile(
                title = "Cyber",
                mode = AppThemeMode.Cyberpunk,
                selected = selected,
                iconRes = R.drawable.ic_lucide_cpu,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )

            ThemeTile(
                title = "Hacker",
                mode = AppThemeMode.Hacker,
                selected = selected,
                iconRes = R.drawable.ic_lucide_terminal_square,
                onSelected = onSelected,
                modifier = Modifier.weight(1f)
            )
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

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.76f)
    }

    Surface(
        modifier = modifier.clickable {
            onSelected(mode)
        },
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shape = MaterialTheme.shapes.large
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
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = title,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
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
