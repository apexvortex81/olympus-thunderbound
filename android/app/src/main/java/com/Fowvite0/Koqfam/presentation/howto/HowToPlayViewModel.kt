package com.Fowvite0.Koqfam.presentation.howto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HowToPlayUiState(
    val telegraphAssist: Boolean = false,
    val swapDashSides: Boolean = false,
    val loaded: Boolean = false
)

class HowToPlayViewModel(
    private val ascent: AscentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HowToPlayUiState())
    val state: StateFlow<HowToPlayUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = ascent.progress()
            _state.update {
                it.copy(
                    telegraphAssist = progress.telegraphAssist,
                    swapDashSides = progress.swapDashSides,
                    loaded = true
                )
            }
        }
    }

    fun toggleTelegraph(enabled: Boolean) {
        viewModelScope.launch {
            ascent.setTelegraphAssist(enabled)
            _state.update { it.copy(telegraphAssist = enabled) }
        }
    }

    fun toggleSwapSides(enabled: Boolean) {
        viewModelScope.launch {
            ascent.setSwapDashSides(enabled)
            _state.update { it.copy(swapDashSides = enabled) }
        }
    }
}
