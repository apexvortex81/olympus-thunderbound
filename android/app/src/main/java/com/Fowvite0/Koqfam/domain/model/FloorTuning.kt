package com.Fowvite0.Koqfam.domain.model

data class FloorTuning(
    val index: Int,
    val rows: Int,
    val boss: Boolean,
    val emitters: Int,
    val driftLedges: Int,
    val windColumns: Int,
    val ledgesPerBand: Int,
    val emitterPeriod: Float
)
