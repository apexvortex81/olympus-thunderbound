package com.Fowvite0.Koqfam.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import com.Fowvite0.Koqfam.domain.repository.LevelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val ascent: AscentRepository,
    private val levels: LevelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = ascent.progress()
            val temples = levels.temples().size
            _state.update {
                it.copy(
                    bestHeightMeters = progress.bestHeightMeters,
                    relicsTotal = progress.relicsTotal,
                    templesCleared = progress.templesCleared,
                    currentFloor = progress.highestUnlockedFloor,
                    currentTemple = (progress.highestUnlockedFloor - 1) / 3 + 1,
                    savedTemples = temples,
                    loaded = true
                )
            }
        }
    }
}
