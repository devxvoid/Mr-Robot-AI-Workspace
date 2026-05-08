package com.mrrobot.aiworkspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.TerminalLine
import com.mrrobot.aiworkspace.data.TerminalLineType
import com.mrrobot.aiworkspace.data.TerminalSamples
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TerminalUiState(
    val input: String = "",
    val logs: List<TerminalLine> = TerminalSamples.initialLogs(),
    val isRunning: Boolean = false
)

class TerminalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    fun updateInput(value: String) {
        _uiState.value = _uiState.value.copy(input = value)
    }

    fun clearLogs() {
        _uiState.value = TerminalUiState(
            logs = listOf(
                line(TerminalLineType.Info, "Terminal cleared.")
            )
        )
    }

    fun runCurrentCommand() {
        val command = _uiState.value.input.trim()
        if (command.isBlank() || _uiState.value.isRunning) return

        _uiState.value = _uiState.value.copy(
            input = "",
            isRunning = true,
            logs = _uiState.value.logs + line(TerminalLineType.Command, "$ $command")
        )

        simulateCommand(command)
    }

    fun runQuickCommand(command: String) {
        if (_uiState.value.isRunning) return

        _uiState.value = _uiState.value.copy(
            isRunning = true,
            logs = _uiState.value.logs + line(TerminalLineType.Command, "$ $command")
        )

        simulateCommand(command)
    }

    private fun simulateCommand(command: String) {
        viewModelScope.launch {
            when {
                command.contains("gradlew", ignoreCase = true) ||
                    command.contains("build", ignoreCase = true) -> {
                    append(TerminalLineType.Output, "Starting Gradle build...")
                    delay(450)
                    append(TerminalLineType.Output, "Configuring Android project :app")
                    delay(450)
                    append(TerminalLineType.Output, "Compiling Kotlin sources")
                    delay(450)
                    append(TerminalLineType.Output, "Packaging debug APK")
                    delay(450)
                    append(TerminalLineType.Success, "BUILD SUCCESSFUL")
                    append(TerminalLineType.Success, "APK: app/build/outputs/apk/debug/app-debug.apk")
                }

                command.contains("test", ignoreCase = true) -> {
                    append(TerminalLineType.Output, "Running unit tests...")
                    delay(450)
                    append(TerminalLineType.Success, "All tests passed.")
                }

                command.contains("openrouter", ignoreCase = true) ||
                    command.contains("ai", ignoreCase = true) -> {
                    append(TerminalLineType.Output, "Checking AI provider configuration...")
                    delay(450)
                    append(TerminalLineType.Info, "OpenRouter key is managed from Settings.")
                    delay(350)
                    append(TerminalLineType.Success, "AI workspace configuration loaded.")
                }

                command.contains("clear", ignoreCase = true) -> {
                    clearLogs()
                }

                command.contains("help", ignoreCase = true) -> {
                    append(TerminalLineType.Info, "Available commands:")
                    append(TerminalLineType.Output, "./gradlew assembleDebug")
                    append(TerminalLineType.Output, "check openrouter")
                    append(TerminalLineType.Output, "run tests")
                    append(TerminalLineType.Output, "clear")
                }

                else -> {
                    append(TerminalLineType.Warning, "Simulated command. Real shell execution is disabled for mobile safety.")
                    delay(350)
                    append(TerminalLineType.Output, "Command received: $command")
                    append(TerminalLineType.Success, "Completed simulation.")
                }
            }

            _uiState.value = _uiState.value.copy(isRunning = false)
        }
    }

    private fun append(type: TerminalLineType, text: String) {
        _uiState.value = _uiState.value.copy(
            logs = _uiState.value.logs + line(type, text)
        )
    }

    private fun line(type: TerminalLineType, text: String): TerminalLine {
        return TerminalLine(
            id = System.nanoTime(),
            type = type,
            text = text,
            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        )
    }
}
