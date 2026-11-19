package com.Fowvite0.Koqfam.domain.model

import kotlin.math.sin

data class Ledge(
    val x: Float,
    val y: Float,
    val widthLanes: Float,
    val drifts: Boolean = false,
    val driftRange: Float = 0f,
    val driftSpeed: Float = 0f,
    val driftPhase: Float = 0f
) {
    fun xAt(time: Float): Float =
        if (!drifts) x else x + sin(time * driftSpeed + driftPhase) * driftRange
}

data class BoltEmitter(
    val lane: Float,
    val y: Float,
    val period: Float,
    val phase: Float
) {
    fun phaseAt(time: Float): Float = (time + phase * period) % period
}

data class WindColumn(
    val x: Float,
    val y: Float,
    val widthLanes: Float,
    val heightRows: Float
)

data class RelicAnchor(
    val x: Float,
    val y: Float
)

data class FloorLayout(
    val index: Int,
    val rows: Int,
    val ledges: List<Ledge>,
    val emitters: List<BoltEmitter>,
    val winds: List<WindColumn>,
    val relics: List<RelicAnchor>,
    val gateX: Float,
    val boss: Boolean,
    val seed: Long,
    val customName: Int = -1
) {
    val gateY: Float get() = rows.toFloat()
}

data class FloorBlueprint(
    val index: Int,
    val rows: Int,
    val boss: Boolean,
    val coreLedges: List<Ledge>,
    val optionalLedges: List<Ledge>,
    val emitters: List<BoltEmitter>,
    val winds: List<WindColumn>,
    val relicAnchors: List<RelicAnchor>,
    val gateX: Float
)
