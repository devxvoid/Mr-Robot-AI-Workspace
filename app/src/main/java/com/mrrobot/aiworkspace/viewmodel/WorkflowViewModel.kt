package com.mrrobot.aiworkspace.viewmodel

import androidx.lifecycle.ViewModel
import com.mrrobot.aiworkspace.data.WorkflowStatus
import com.mrrobot.aiworkspace.data.WorkflowStep
import com.mrrobot.aiworkspace.data.WorkflowTemplates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WorkflowUiState(
    val workflowName: String = "Mr. Robot Build Pipeline",
    val customTitle: String = "",
    val customDescription: String = "",
    val customAgent: String = "Android Architect",
    val steps: List<WorkflowStep> = WorkflowTemplates.androidAppBuild(),
    val generatedPrompt: String = ""
)

class WorkflowViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkflowUiState())
    val uiState: StateFlow<WorkflowUiState> = _uiState.asStateFlow()

    fun updateWorkflowName(value: String) {
        _uiState.value = _uiState.value.copy(workflowName = value)
    }

    fun updateCustomTitle(value: String) {
        _uiState.value = _uiState.value.copy(customTitle = value)
    }

    fun updateCustomDescription(value: String) {
        _uiState.value = _uiState.value.copy(customDescription = value)
    }

    fun updateCustomAgent(value: String) {
        _uiState.value = _uiState.value.copy(customAgent = value)
    }

    fun loadAndroidTemplate() {
        _uiState.value = _uiState.value.copy(
            workflowName = "Android App Build Pipeline",
            steps = WorkflowTemplates.androidAppBuild(),
            generatedPrompt = ""
        )
    }

    fun loadAiWorkspaceTemplate() {
        _uiState.value = _uiState.value.copy(
            workflowName = "AI Workspace Upgrade Pipeline",
            steps = WorkflowTemplates.aiWorkspaceUpgrade(),
            generatedPrompt = ""
        )
    }

    fun clearSteps() {
        _uiState.value = _uiState.value.copy(
            steps = emptyList(),
            generatedPrompt = ""
        )
    }

    fun addCustomStep() {
        val state = _uiState.value
        if (state.customTitle.isBlank()) return

        val nextId = (state.steps.maxOfOrNull { it.id } ?: 0L) + 1L

        val step = WorkflowStep(
            id = nextId,
            title = state.customTitle.trim(),
            description = state.customDescription.trim().ifBlank {
                "Custom workflow task."
            },
            agent = state.customAgent.trim().ifBlank {
                "Android Architect"
            }
        )

        _uiState.value = state.copy(
            steps = state.steps + step,
            customTitle = "",
            customDescription = "",
            generatedPrompt = ""
        )
    }

    fun removeStep(id: Long) {
        _uiState.value = _uiState.value.copy(
            steps = _uiState.value.steps.filterNot { it.id == id },
            generatedPrompt = ""
        )
    }

    fun moveStepUp(id: Long) {
        val steps = _uiState.value.steps.toMutableList()
        val index = steps.indexOfFirst { it.id == id }

        if (index > 0) {
            val item = steps.removeAt(index)
            steps.add(index - 1, item)
            _uiState.value = _uiState.value.copy(
                steps = steps,
                generatedPrompt = ""
            )
        }
    }

    fun moveStepDown(id: Long) {
        val steps = _uiState.value.steps.toMutableList()
        val index = steps.indexOfFirst { it.id == id }

        if (index >= 0 && index < steps.lastIndex) {
            val item = steps.removeAt(index)
            steps.add(index + 1, item)
            _uiState.value = _uiState.value.copy(
                steps = steps,
                generatedPrompt = ""
            )
        }
    }

    fun markStepRunning(id: Long) {
        updateStatus(id, WorkflowStatus.Running)
    }

    fun markStepCompleted(id: Long) {
        updateStatus(id, WorkflowStatus.Completed)
    }

    fun markStepFailed(id: Long) {
        updateStatus(id, WorkflowStatus.Failed)
    }

    private fun updateStatus(id: Long, status: WorkflowStatus) {
        _uiState.value = _uiState.value.copy(
            steps = _uiState.value.steps.map {
                if (it.id == id) it.copy(status = status) else it
            },
            generatedPrompt = ""
        )
    }

    fun generatePrompt() {
        val state = _uiState.value

        val stepText = state.steps.mapIndexed { index, step ->
            """
            ## Step ${index + 1}: ${step.title}
            Agent: ${step.agent}
            Status: ${step.status}
            Task: ${step.description}
            """.trimIndent()
        }.joinToString(separator = "\n\n")

        val prompt = """
        # ${state.workflowName}

        You are Mr. Robot AI Workspace.

        Execute this workflow as a coordinated multi-agent Android development pipeline.

        Requirements:
        - Provide complete implementation-ready output.
        - Use exact file paths.
        - Provide full code when code is needed.
        - Avoid vague placeholders.
        - Prioritize production-ready Android MVP quality.
        - Use Kotlin, Jetpack Compose, Material 3, MVVM, Coroutines, DataStore, and GitHub Actions where relevant.

        $stepText

        Final Output:
        - Summarize completed work.
        - Provide next action.
        - Mention risks or missing requirements.
        """.trimIndent()

        _uiState.value = state.copy(generatedPrompt = prompt)
    }
}
