package com.Fowvite0.Koqfam.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.ClimbState
import com.Fowvite0.Koqfam.domain.model.FloorLayout
import com.Fowvite0.Koqfam.domain.model.Outcome
import com.Fowvite0.Koqfam.domain.model.RunPhase
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import com.Fowvite0.Koqfam.domain.repository.LevelRepository
import com.Fowvite0.Koqfam.domain.usecase.GenerateFloorUseCase
import com.Fowvite0.Koqfam.domain.usecase.StepClimbUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val ascent: AscentRepository,
    private val levels: LevelRepository,
    private val generateFloor: GenerateFloorUseCase,
    private val stepClimb: StepClimbUseCase,
    private val floorIndex: Int,
    private val customSlot: Int,
    private val attempt: Int
) : ViewModel() {

    private val _state = MutableStateFlow(
        GameUiState(floorIndex = floorIndex, customSlot = customSlot)
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val results = Channel<RunResult>(Channel.BUFFERED)
    val events: Flow<RunResult> = results.receiveAsFlow()

    private var loop: Job? = null

    init {
        viewModelScope.launch {
            val progress = ascent.progress()
            val layout = buildLayout()
            _state.update {
                it.copy(
                    layout = layout,
                    climb = initialClimb(layout),
                    bestHeightMeters = progress.bestHeightMeters,
                    telegraphAssist = progress.telegraphAssist,
                    swapDashSides = progress.swapDashSides,
                    ready = true
                )
            }
        }
    }

    fun begin() {
        val current = _state.value
        if (current.layout == null || current.climb.phase != RunPhase.IDLE) return
        _state.update { it.copy(climb = it.climb.copy(phase = RunPhase.RUNNING), paused = false) }
        runLoop()
    }

    fun dash(toRight: Boolean) {
        val current = _state.value
        if (!current.running || current.paused) return
        val climb = current.climb
        if (climb.charges <= 0) return
        val wasFull = climb.charges >= GameConfig.MAX_CHARGES
        _state.update {
            it.copy(
                climb = climb.copy(
                    charges = climb.charges - 1,
                    chargeTimer = if (wasFull) GameConfig.CHARGE_REFILL_SECONDS else climb.chargeTimer,
                    velocityX = if (toRight) GameConfig.DASH_VX else -GameConfig.DASH_VX,
                    velocityY = GameConfig.DASH_VY,
                    grounded = false,
                    facingRight = toRight,
                    dashTrail = GameConfig.DASH_TRAIL_SECONDS
                )
            )
        }
    }

    fun raiseAegis() {
        val current = _state.value
        if (!current.running || current.paused) return
        val climb = current.climb
        if (climb.bubbles <= 0 || climb.shieldRemaining > 0f) return
        _state.update {
            it.copy(
                climb = climb.copy(
                    bubbles = climb.bubbles - 1,
                    shieldRemaining = GameConfig.SHIELD_SECONDS
                )
            )
        }
    }

    fun pause() {
        loop?.cancel()
        loop = null
        if (_state.value.running) {
            _state.update { it.copy(paused = true) }
        }
    }

    fun resume() {
        if (!_state.value.paused) return
        _state.update { it.copy(paused = false) }
        runLoop()
    }

    override fun onCleared() {
        loop?.cancel()
        loop = null
        super.onCleared()
    }

    private fun runLoop() {
        loop?.cancel()
        loop = viewModelScope.launch {
            while (isActive) {
                val current = _state.value
                val layout = current.layout
                if (layout == null || current.paused) break
                val next = stepClimb(current.climb, layout, current.telegraphAssist)
                _state.update { it.copy(climb = next) }
                if (next.phase == RunPhase.FINISHED) {
                    publish(next, layout)
                    break
                }
                delay(GameConfig.TICK_MS)
            }
        }
    }

    private fun publish(climb: ClimbState, layout: FloorLayout) {
        val height = (climb.maxRows * GameConfig.METERS_PER_ROW).toInt().coerceAtLeast(0)
        val outcome = climb.outcome ?: Outcome.STORM
        val cleared = outcome != Outcome.STORM
        if (customSlot < 0) {
            ascent.recordRun(layout.index, height, climb.relicsFound, cleared)
        }
        results.trySend(RunResult(outcome, height, climb.relicsFound))
    }

    private fun buildLayout(): FloorLayout {
        if (customSlot >= 0) {
            val temple = levels.temple(customSlot) ?: levels.draft()
            if (temple != null) return generateFloor.fromTemple(temple, customSlot)
        }
        val index = if (floorIndex < 1) 1 else floorIndex
        return generateFloor(levels.tuning(index), attempt)
    }

    private fun initialClimb(layout: FloorLayout): ClimbState = ClimbState(
        phase = RunPhase.IDLE,
        heroX = layout.ledges.first().x,
        heroY = layout.ledges.first().y,
        grounded = true,
        ledgeIndex = 0,
        facingRight = true,
        cameraY = GameConfig.VISIBLE_ROWS * 0.42f,
        charges = GameConfig.MAX_CHARGES,
        chargeTimer = GameConfig.CHARGE_REFILL_SECONDS,
        bubbles = GameConfig.MAX_BUBBLES,
        relicTaken = List(layout.relics.size) { false },
        tideY = GameConfig.TIDE_BEGIN_ROWS,
        sentinelY = layout.rows - 1.5f
    )
}
