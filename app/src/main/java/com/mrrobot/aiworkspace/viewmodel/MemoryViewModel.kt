package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.AgentConfigStore
import com.mrrobot.aiworkspace.data.MemoryCategory
import com.mrrobot.aiworkspace.data.MemoryEntry
import com.mrrobot.aiworkspace.data.MemoryStore
import com.mrrobot.aiworkspace.data.SoulConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MemoryUiState(
    val memories: List<MemoryEntry> = emptyList(),
    val showEditor: Boolean = false,
    val editingKey: String? = null,
    val editorKey: String = "",
    val editorContent: String = "",
    val editorCategory: MemoryCategory = MemoryCategory.GENERAL,
    val savedMessage: String = ""
) {
    val promotionCandidates: List<MemoryEntry>
        get() = memories.filter { it.hitCount >= 5 }
}

class MemoryViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryStore = MemoryStore(application.applicationContext)
    private val agentConfigStore = AgentConfigStore(application.applicationContext)

    private val _uiState = MutableStateFlow(MemoryUiState())
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            memoryStore.memoriesFlow.collect { memories ->
                _uiState.value = _uiState.value.copy(
                    memories = memories.sortedByDescending { it.updatedAt }
                )
            }
        }
    }

    fun showCreate() {
        _uiState.value = _uiState.value.copy(
            showEditor = true,
            editingKey = null,
            editorKey = "",
            editorContent = "",
            editorCategory = MemoryCategory.GENERAL
        )
    }

    fun showEdit(memory: MemoryEntry) {
        _uiState.value = _uiState.value.copy(
            showEditor = true,
            editingKey = memory.key,
            editorKey = memory.key,
            editorContent = memory.content,
            editorCategory = memory.category
        )
    }

    fun dismissEditor() {
        _uiState.value = _uiState.value.copy(showEditor = false)
    }

    fun updateEditorKey(value: String) {
        _uiState.value = _uiState.value.copy(editorKey = value)
    }

    fun updateEditorContent(value: String) {
        _uiState.value = _uiState.value.copy(editorContent = value)
    }

    fun updateEditorCategory(value: MemoryCategory) {
        _uiState.value = _uiState.value.copy(editorCategory = value)
    }

    fun save() {
        val state = _uiState.value
        if (state.editorKey.isBlank()) {
            _uiState.value = state.copy(savedMessage = "Memory key required.")
            return
        }
        if (state.editorContent.isBlank()) {
            _uiState.value = state.copy(savedMessage = "Memory content required.")
            return
        }

        viewModelScope.launch {
            memoryStore.store(
                key = state.editorKey,
                content = state.editorContent,
                category = state.editorCategory
            )
            _uiState.value = _uiState.value.copy(
                showEditor = false,
                savedMessage = if (state.editingKey == null) "Memory created." else "Memory updated."
            )
        }
    }

    fun forget(memory: MemoryEntry) {
        viewModelScope.launch {
            memoryStore.forget(memory.key)
            _uiState.value = _uiState.value.copy(savedMessage = "Forgot \"${memory.key}\"")
        }
    }

    fun reinforce(memory: MemoryEntry) {
        viewModelScope.launch {
            memoryStore.reinforce(memory.key)
            _uiState.value = _uiState.value.copy(savedMessage = "Reinforced.")
        }
    }

    fun forgetAll() {
        viewModelScope.launch {
            memoryStore.forgetAll()
            _uiState.value = _uiState.value.copy(savedMessage = "All memories cleared.")
        }
    }

    /**
     * Promote a memory into the soul: append it to the user's custom system prompt
     * and remove it from the memory store. Mirrors Kai's "promotion" flow.
     */
    fun promoteToSoul(memory: MemoryEntry) {
        viewModelScope.launch {
            val soul = agentConfigStore.getSoul()
            val promoted = buildString {
                append(soul.customPrompt.ifBlank { SoulConfig.DEFAULT_SOUL_PROMPT })
                append("\n\n[")
                append(memory.category.name)
                append("] ")
                append(memory.key)
                append(": ")
                append(memory.content)
            }
            agentConfigStore.saveSoul(SoulConfig(customPrompt = promoted))
            memoryStore.forget(memory.key)
            _uiState.value = _uiState.value.copy(
                savedMessage = "Promoted \"${memory.key}\" to soul."
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(savedMessage = "")
    }
}
