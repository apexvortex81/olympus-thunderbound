package com.Fowvite0.Koqfam.presentation.game

import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.ClimbState
import com.Fowvite0.Koqfam.domain.model.FloorLayout
import com.Fowvite0.Koqfam.domain.model.Outcome
import com.Fowvite0.Koqfam.domain.model.RunPhase

data class GameUiState(
    val layout: FloorLayout? = null,
    val climb: ClimbState = ClimbState(),
    val floorIndex: Int = 1,
    val customSlot: Int = -1,
    val bestHeightMeters: Int = 0,
    val paused: Boolean = false,
    val telegraphAssist: Boolean = false,
    val swapDashSides: Boolean = false,
    val ready: Boolean = false
) {
    val heightMeters: Int
        get() = (climb.maxRows * GameConfig.METERS_PER_ROW).toInt().coerceAtLeast(0)

    val idle: Boolean get() = climb.phase == RunPhase.IDLE

    val running: Boolean get() = climb.phase == RunPhase.RUNNING

    val chargeFill: Float
        get() = if (climb.charges >= GameConfig.MAX_CHARGES) {
            0f
        } else {
            1f - (climb.chargeTimer / GameConfig.CHARGE_REFILL_SECONDS).coerceIn(0f, 1f)
        }

    val shieldUp: Boolean get() = climb.shieldRemaining > 0f

    val showHint: Boolean get() = idle || climb.runTime < GameConfig.HINT_SECONDS
}

data class RunResult(
    val outcome: Outcome,
    val heightMeters: Int,
    val relics: Int
)
