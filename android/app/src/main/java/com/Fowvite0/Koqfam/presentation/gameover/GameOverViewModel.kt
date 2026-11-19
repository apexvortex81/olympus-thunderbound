package com.Fowvite0.Koqfam.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.Outcome
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import com.Fowvite0.Koqfam.domain.usecase.ScoreRunUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameOverUiState(
    val outcome: Outcome = Outcome.STORM,
    val heightMeters: Int = 0,
    val relics: Int = 0,
    val bestHeightMeters: Int = 0,
    val score: Int = 0,
    val isNewBest: Boolean = false,
    val nextFloor: Int = -1,
    val loaded: Boolean = false
)

class GameOverViewModel(
    private val ascent: AscentRepository,
    private val scoreRun: ScoreRunUseCase,
    private val floorIndex: Int,
    private val customSlot: Int,
    private val outcome: Outcome,
    private val heightMeters: Int,
    private val relics: Int
) : ViewModel() {

    private val _state = MutableStateFlow(
        GameOverUiState(outcome = outcome, heightMeters = heightMeters, relics = relics)
    )
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val progress = ascent.progress()
            val cleared = outcome != Outcome.STORM
            val next = if (cleared && customSlot < 0 && floorIndex in 1 until GameConfig.FLOOR_COUNT) {
                floorIndex + 1
            } else {
                -1
            }
            _state.update {
                it.copy(
                    bestHeightMeters = progress.bestHeightMeters,
                    score = scoreRun(heightMeters, relics, cleared),
                    isNewBest = heightMeters > 0 && heightMeters >= progress.bestHeightMeters,
                    nextFloor = next,
                    loaded = true
                )
            }
        }
    }
}
