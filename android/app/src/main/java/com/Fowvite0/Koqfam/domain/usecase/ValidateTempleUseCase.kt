package com.Fowvite0.Koqfam.domain.usecase

import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.TemplePiece
import com.Fowvite0.Koqfam.domain.model.TempleValidation
import kotlin.math.abs

class ValidateTempleUseCase {

    operator fun invoke(temple: CustomTemple): TempleValidation {
        val gate = findAll(temple, TemplePiece.GATE)
        if (gate.isEmpty()) return TempleValidation(false, missingGate = true)

        val supports = findAll(temple, TemplePiece.BASALT) + findAll(temple, TemplePiece.DRIFT)
        if (supports.isEmpty()) return TempleValidation(false, missingLedge = true)

        val winds = findAll(temple, TemplePiece.WIND)
        val nodes = supports.map { it.first + 0.5f to (temple.rows - 1 - it.second).toFloat() }
        val start = 3.5f to 0f
        val all = listOf(start) + nodes
        val reached = BooleanArray(all.size)
        reached[0] = true
        val queue = ArrayDeque<Int>()
        queue.add(0)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            all.forEachIndexed { index, node ->
                if (!reached[index] && reachable(all[current], node, winds, temple)) {
                    reached[index] = true
                    queue.add(index)
                }
            }
        }

        val gateNode = gate.first().first + 0.5f to (temple.rows - 1 - gate.first().second).toFloat()
        val gateOk = all.indices.any { index ->
            reached[index] && gateNode.second - all[index].second <= GameConfig.MAX_DASH_REACH_ROWS &&
                abs(gateNode.first - all[index].first) <= GameConfig.MAX_DASH_REACH_LANES + 0.3f
        }
        if (gateOk) return TempleValidation(true)

        val highestReached = all.indices.filter { reached[it] }.maxOf { all[it].second }
        val breakRow = (temple.rows - highestReached.toInt()).coerceIn(1, temple.rows)
        return TempleValidation(false, breakRow = breakRow)
    }

    private fun reachable(
        from: Pair<Float, Float>,
        target: Pair<Float, Float>,
        winds: List<Pair<Int, Int>>,
        temple: CustomTemple
    ): Boolean {
        val rise = target.second - from.second
        val gap = abs(target.first - from.first)
        if (rise <= 0f) return gap <= 3.2f && rise > -5f
        if (rise <= GameConfig.MAX_DASH_REACH_ROWS && gap <= GameConfig.MAX_DASH_REACH_LANES + 0.3f) {
            return true
        }
        return winds.any { wind ->
            val windX = wind.first + 0.5f
            val windY = (temple.rows - 1 - wind.second).toFloat()
            abs(windX - from.first) <= 1.6f &&
                from.second >= windY - 2f &&
                target.second <= windY + 5.5f &&
                gap <= 3.0f
        }
    }

    private fun findAll(temple: CustomTemple, piece: TemplePiece): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (row in 0 until temple.rows) {
            for (col in 0 until temple.cols) {
                if (temple.at(col, row) == piece) result.add(col to row)
            }
        }
        return result
    }
}
