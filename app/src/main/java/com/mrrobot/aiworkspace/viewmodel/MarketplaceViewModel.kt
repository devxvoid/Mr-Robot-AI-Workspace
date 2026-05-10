package com.mrrobot.aiworkspace.viewmodel

import androidx.lifecycle.ViewModel
import com.mrrobot.aiworkspace.data.MarketplaceCatalog
import com.mrrobot.aiworkspace.data.MarketplaceItem
import com.mrrobot.aiworkspace.data.MarketplaceItemType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MarketplaceUiState(
    val items: List<MarketplaceItem> = MarketplaceCatalog.items(),
    val query: String = "",
    val selectedType: MarketplaceItemType? = null,
    val selectedItem: MarketplaceItem? = null
) {
    val filteredItems: List<MarketplaceItem>
        get() {
            return items.filter { item ->
                val matchesQuery =
                    query.isBlank() ||
                        item.title.contains(query, ignoreCase = true) ||
                        item.provider.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true) ||
                        item.tags.any { it.contains(query, ignoreCase = true) }

                val matchesType =
                    selectedType == null || item.type == selectedType

                matchesQuery && matchesType
            }
        }

    val enabledCount: Int
        get() = items.count { it.enabled }
}

class MarketplaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    fun updateQuery(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
    }

    fun selectType(type: MarketplaceItemType?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    fun selectItem(item: MarketplaceItem) {
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }

    fun closeDetails() {
        _uiState.value = _uiState.value.copy(selectedItem = null)
    }

    fun toggleItem(id: String) {
        val current = _uiState.value

        val updated = current.items.map { item ->
            if (item.id == id) {
                item.copy(enabled = !item.enabled)
            } else {
                item
            }
        }

        _uiState.value = current.copy(
            items = updated,
            selectedItem = updated.firstOrNull { it.id == id }
        )
    }
}
