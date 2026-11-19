package com.Fowvite0.Koqfam.data.sample

import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.FloorTuning
import com.Fowvite0.Koqfam.domain.model.TemplePiece

object SampleData {

    val stormFloors: List<FloorTuning> = listOf(
        FloorTuning(1, 18, false, 2, 1, 1, 2, 3.4f),
        FloorTuning(2, 18, false, 3, 1, 1, 2, 3.2f),
        FloorTuning(3, 20, true, 3, 1, 1, 2, 3.0f),
        FloorTuning(4, 18, false, 4, 2, 1, 2, 3.0f),
        FloorTuning(5, 18, false, 5, 2, 2, 2, 2.9f),
        FloorTuning(6, 20, true, 5, 2, 2, 2, 2.8f),
        FloorTuning(7, 18, false, 6, 3, 2, 2, 2.7f),
        FloorTuning(8, 18, false, 6, 4, 2, 2, 2.6f),
        FloorTuning(9, 20, true, 7, 4, 2, 2, 2.5f)
    )

    fun tuning(index: Int): FloorTuning =
        stormFloors.firstOrNull { it.index == index } ?: stormFloors.first()

    val firstTemple: CustomTemple = buildFirstTemple()

    private fun buildFirstTemple(): CustomTemple {
        var temple = CustomTemple.empty(0, 7, 14)
        val ledges = listOf(2 to 12, 4 to 10, 2 to 8, 4 to 6, 2 to 4, 3 to 2)
        ledges.forEach { pair -> temple = temple.with(pair.first, pair.second, TemplePiece.BASALT) }
        temple = temple.with(5, 10, TemplePiece.DRIFT)
        temple = temple.with(0, 7, TemplePiece.WIND)
        temple = temple.with(6, 5, TemplePiece.BOLT)
        temple = temple.with(1, 3, TemplePiece.RELIC)
        temple = temple.with(3, 0, TemplePiece.GATE)
        return temple
    }
}
