package com.Fowvite0.Koqfam.domain.model

enum class Outcome {
    AEGIS,
    SENTINEL,
    STORM
}

enum class RunPhase {
    IDLE,
    RUNNING,
    ENDING,
    FINISHED
}

data class ClimbState(
    val phase: RunPhase = RunPhase.IDLE,
    val heroX: Float = 0f,
    val heroY: Float = 0f,
    val velocityX: Float = 0f,
    val velocityY: Float = 0f,
    val grounded: Boolean = true,
    val ledgeIndex: Int = 0,
    val facingRight: Boolean = true,
    val cameraY: Float = 0f,
    val runTime: Float = 0f,
    val charges: Int = 0,
    val chargeTimer: Float = 0f,
    val bubbles: Int = 0,
    val shieldRemaining: Float = 0f,
    val dashTrail: Float = 0f,
    val relicTaken: List<Boolean> = emptyList(),
    val relicsFound: Int = 0,
    val tideY: Float = 0f,
    val maxRows: Float = 0f,
    val sentinelY: Float = 0f,
    val sentinelHits: Int = 0,
    val sentinelCooldown: Float = 0f,
    val dissolve: Float = 0f,
    val flash: Float = 0f,
    val winGlow: Float = 0f,
    val outcome: Outcome? = null
)

data class TempleValidation(
    val valid: Boolean,
    val missingGate: Boolean = false,
    val missingLedge: Boolean = false,
    val breakRow: Int = -1
)
