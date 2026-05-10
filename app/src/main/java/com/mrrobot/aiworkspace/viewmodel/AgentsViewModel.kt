package com.mrrobot.aiworkspace.viewmodel

import androidx.lifecycle.ViewModel
import com.mrrobot.aiworkspace.data.Agent
import com.mrrobot.aiworkspace.data.AgentCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AgentsUiState(
    val agents: List<Agent> = AgentCatalog.agents,
    val selectedAgent: Agent = AgentCatalog.agents.first(),
    val generatedPrompt: String = "",
    val taskInput: String = ""
)

class AgentsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AgentsUiState())
    val uiState: StateFlow<AgentsUiState> = _uiState.asStateFlow()

    fun selectAgent(agent: Agent) {
        _uiState.value = _uiState.value.copy(
            selectedAgent = agent,
            generatedPrompt = ""
        )
    }

    fun updateTask(value: String) {
        _uiState.value = _uiState.value.copy(
            taskInput = value,
            generatedPrompt = ""
        )
    }

    fun generatePrompt() {
        val state = _uiState.value
        val agent = state.selectedAgent
        val task = state.taskInput.ifBlank {
            "Improve the Mr. Robot AI Workspace Android app."
        }

        val prompt = """
        # Agent Task

        You are ${agent.name}.

        ## Role
        ${agent.role}

        ## System Behavior
        ${agent.systemPrompt}

        ## Task
        $task

        ## Required Output
        - Give production-ready implementation.
        - Provide full code, not snippets.
        - Explain exact file paths.
        - Avoid placeholders.
        - Optimize for Android MVP quality.

        ## Skills To Apply
        ${agent.skills.joinToString(separator = "\n") { "- $it" }}
        """.trimIndent()

        _uiState.value = state.copy(
            generatedPrompt = prompt
        )
    }

    fun clear() {
        _uiState.value = _uiState.value.copy(
            taskInput = "",
            generatedPrompt = ""
        )
    }
}
