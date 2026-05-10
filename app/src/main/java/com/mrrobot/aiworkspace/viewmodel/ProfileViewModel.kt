package com.mrrobot.aiworkspace.viewmodel

import androidx.lifecycle.ViewModel
import com.mrrobot.aiworkspace.data.ProfileDefaults
import com.mrrobot.aiworkspace.data.WorkspaceCapability
import com.mrrobot.aiworkspace.data.WorkspaceProfile
import com.mrrobot.aiworkspace.data.WorkspaceStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val profile: WorkspaceProfile = ProfileDefaults.profile(),
    val stats: List<WorkspaceStat> = ProfileDefaults.stats(),
    val capabilities: List<WorkspaceCapability> = ProfileDefaults.capabilities(),
    val exportText: String = "",
    val copiedMessage: String = ""
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun generateExport() {
        val state = _uiState.value
        val profile = state.profile

        val statsText = state.stats.joinToString("\n") {
            "- ${it.label}: ${it.value} — ${it.description}"
        }

        val capabilitiesText = state.capabilities.joinToString("\n") {
            "- ${it.name}: ${if (it.enabled) "Enabled" else "Disabled"} — ${it.description}"
        }

        val export = """
        # ${profile.displayName} AI Workspace

        ## Identity
        Name: ${profile.displayName}
        Title: ${profile.title}
        Handle: ${profile.handle}
        Status: ${profile.status}

        ## Principle
        ${profile.principle}

        ## Links
        GitHub: ${profile.github}
        Website: ${profile.website}
        X: ${profile.xProfile}

        ## Workspace Stats
        $statsText

        ## Capabilities
        $capabilitiesText

        ## Build Status
        Android Compose MVP foundation is active.
        OpenRouter chat, agents, workflow builder, terminal logs, file manager, marketplace, settings, and profile systems are integrated.
        """.trimIndent()

        _uiState.value = state.copy(
            exportText = export,
            copiedMessage = ""
        )
    }

    fun clearExport() {
        _uiState.value = _uiState.value.copy(
            exportText = "",
            copiedMessage = ""
        )
    }

    fun markCopied() {
        _uiState.value = _uiState.value.copy(
            copiedMessage = "Profile export copied"
        )
    }
}
