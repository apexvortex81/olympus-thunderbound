package com.Fowvite0.Koqfam.presentation.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.FloorTile
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import com.Fowvite0.Koqfam.domain.repository.LevelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LevelSelectUiState(
    val floors: List<FloorTile> = emptyList(),
    val temples: List<CustomTemple> = emptyList(),
    val loaded: Boolean = false
)

class LevelSelectViewModel(
    private val ascent: AscentRepository,
    private val levels: LevelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LevelSelectUiState())
    val state: StateFlow<LevelSelectUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = ascent.progress()
            val tiles = (1..GameConfig.FLOOR_COUNT).map { index ->
                FloorTile(
                    index = index,
                    unlocked = index <= progress.highestUnlockedFloor,
                    cleared = index <= progress.floorsCleared,
                    boss = index % 3 == 0,
                    bestHeightMeters = ascent.floorBest(index),
                    relicsFound = ascent.floorRelics(index)
                )
            }
            _state.update {
                it.copy(floors = tiles, temples = levels.temples(), loaded = true)
            }
        }
    }
}
