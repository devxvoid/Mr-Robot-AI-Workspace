package com.mrrobot.aiworkspace.viewmodel

import androidx.lifecycle.ViewModel
import com.mrrobot.aiworkspace.data.WorkspaceFile
import com.mrrobot.aiworkspace.data.WorkspaceFileType
import com.mrrobot.aiworkspace.data.WorkspaceFiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FileManagerUiState(
    val allFiles: List<WorkspaceFile> = WorkspaceFiles.samples(),
    val query: String = "",
    val selectedType: WorkspaceFileType? = null,
    val selectedFile: WorkspaceFile? = null,
    val copiedMessage: String = ""
) {
    val filteredFiles: List<WorkspaceFile>
        get() {
            return allFiles.filter { file ->
                val matchesQuery =
                    query.isBlank() ||
                        file.name.contains(query, ignoreCase = true) ||
                        file.path.contains(query, ignoreCase = true) ||
                        file.description.contains(query, ignoreCase = true)

                val matchesType =
                    selectedType == null || file.type == selectedType

                matchesQuery && matchesType
            }
        }
}

class FileManagerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FileManagerUiState())
    val uiState: StateFlow<FileManagerUiState> = _uiState.asStateFlow()

    fun updateQuery(value: String) {
        _uiState.value = _uiState.value.copy(
            query = value,
            copiedMessage = ""
        )
    }

    fun selectType(type: WorkspaceFileType?) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            copiedMessage = ""
        )
    }

    fun selectFile(file: WorkspaceFile) {
        _uiState.value = _uiState.value.copy(
            selectedFile = file,
            copiedMessage = ""
        )
    }

    fun closePreview() {
        _uiState.value = _uiState.value.copy(
            selectedFile = null
        )
    }

    fun markCopied(label: String) {
        _uiState.value = _uiState.value.copy(
            copiedMessage = "$label copied"
        )
    }
}
