package com.Fowvite0.Koqfam.domain.usecase

import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.BoltEmitter
import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.FloorLayout
import com.Fowvite0.Koqfam.domain.model.FloorTuning
import com.Fowvite0.Koqfam.domain.model.Ledge
import com.Fowvite0.Koqfam.domain.model.RelicAnchor
import com.Fowvite0.Koqfam.domain.model.TemplePiece
import com.Fowvite0.Koqfam.domain.model.WindColumn
import kotlin.math.abs
import kotlin.random.Random

class GenerateFloorUseCase {

    operator fun invoke(tuning: FloorTuning, attempt: Int): FloorLayout {
        val baseSeed = tuning.index * 7919L xor (attempt + 1) * 104729L
        for (pass in 0 until GameConfig.GENERATION_ATTEMPTS) {
            val seed = baseSeed + pass * 7717L
            val layout = build(tuning, seed)
            if (solvable(layout)) return layout
        }
        return fallback(tuning, baseSeed)
    }

    fun fromTemple(temple: CustomTemple, slot: Int): FloorLayout {
        val rows = temple.rows
        val ledges = mutableListOf(Ledge(3.5f, 0f, 3.2f))
        val emitters = mutableListOf<BoltEmitter>()
        val winds = mutableListOf<WindColumn>()
        val relics = mutableListOf<RelicAnchor>()
        var gateX = 3.5f
        var gateY = rows.toFloat()
        for (row in 0 until temple.rows) {
            for (col in 0 until temple.cols) {
                val piece = temple.at(col, row)
                if (piece == TemplePiece.EMPTY) continue
                val worldX = col + 0.5f
                val worldY = (rows - 1 - row).toFloat()
                when (piece) {
                    TemplePiece.BASALT -> ledges.add(Ledge(worldX, worldY, 1.9f))
                    TemplePiece.DRIFT -> ledges.add(
                        Ledge(worldX, worldY, 1.9f, true, 0.6f, 0.9f, worldY)
                    )

                    TemplePiece.BOLT -> emitters.add(
                        BoltEmitter(worldX, worldY, 3.0f, (col * 0.17f) % 1f)
                    )

                    TemplePiece.WIND -> winds.add(
                        WindColumn(worldX, worldY + 0.3f, 1.3f, 4.5f)
                    )

                    TemplePiece.RELIC -> relics.add(RelicAnchor(worldX, worldY + 0.8f))
                    TemplePiece.GATE -> {
                        gateX = worldX
                        gateY = worldY
                    }

                    TemplePiece.EMPTY -> Unit
                }
            }
        }
        return FloorLayout(
            index = 0,
            rows = gateY.toInt().coerceAtLeast(4),
            ledges = ledges,
            emitters = emitters,
            winds = winds,
            relics = relics,
            gateX = gateX,
            boss = false,
            seed = slot.toLong(),
            customName = slot
        )
    }

    private fun build(tuning: FloorTuning, seed: Long): FloorLayout {
        val random = Random(seed)
        val rows = tuning.rows
        val reach = GameConfig.DASH_REACH_LANES
        val rise = GameConfig.DASH_RISE_ROWS
        val low = GameConfig.LANE_MARGIN
        val high = GameConfig.LANES - GameConfig.LANE_MARGIN

        val ledges = mutableListOf(Ledge(3.5f, 0f, 3.2f))
        val relics = mutableListOf<RelicAnchor>()
        val winds = mutableListOf<WindColumn>()
        val emitters = mutableListOf<BoltEmitter>()

        var band = listOf(3.5f)
        var y = 0f
        var driftLeft = tuning.driftLedges
        var windLeft = tuning.windColumns
        val relicLeft = GameConfig.RELIC_PER_FLOOR
        val relicSpots = mutableListOf<RelicAnchor>()

        while (y + rise * 3f <= rows) {
            val fromBand = band
            val next = mutableListOf<Float>()
            fromBand.forEach { anchor ->
                listOf(anchor - reach, anchor + reach).forEach { candidate ->
                    if (candidate >= low && candidate <= high &&
                        next.none { abs(it - candidate) < 0.8f }
                    ) {
                        next.add(candidate)
                    }
                }
            }
            if (next.isEmpty()) next.add(3.5f)

            val narrow = tuning.ledgesPerBand < 2 || random.nextFloat() < 0.22f
            val keep = if (next.size > 1 && narrow) {
                listOf(next[random.nextInt(next.size)])
            } else {
                next
            }

            fromBand.forEach { anchor ->
                val side = if (random.nextBoolean()) 1f else -1f
                relicSpots.add(RelicAnchor(anchor + side * reach * 0.72f, y + rise * 1.1f))
            }

            y += rise
            val placed = mutableListOf<Float>()
            keep.forEach { centre ->
                val drifts = keep.size > 1 && driftLeft > 0 && random.nextFloat() < 0.5f
                if (drifts) driftLeft -= 1
                val jitter = (random.nextFloat() - 0.5f) * 0.5f
                val x = (centre + jitter).coerceIn(low, high)
                placed.add(x)
                ledges.add(
                    Ledge(
                        x = x,
                        y = y,
                        widthLanes = 2.1f + random.nextFloat() * 0.6f,
                        drifts = drifts,
                        driftRange = if (drifts) 0.55f else 0f,
                        driftSpeed = if (drifts) 0.8f + random.nextFloat() * 0.5f else 0f,
                        driftPhase = random.nextFloat() * 6.2f
                    )
                )
            }
            band = placed

            if (windLeft > 0 && y > 2f && random.nextFloat() < 0.35f) {
                windLeft -= 1
                winds.add(WindColumn(placed.first(), y + 0.35f, 1.3f, rise * 2.4f))
            }
        }

        val topAnchor = band[random.nextInt(band.size)]
        val topOptions = listOf(topAnchor - reach, topAnchor + reach)
            .filter { it >= low && it <= high }
        val topX = if (topOptions.isEmpty()) {
            topAnchor
        } else {
            topOptions[random.nextInt(topOptions.size)]
        }
        val topY = y + rise
        ledges.add(Ledge(topX, topY, 2.6f))

        val emitterRows = generateSequence(3.5f) { it + 2f }
            .takeWhile { it <= rows - 2.5f }
            .toList()
        val chosenRows = emitterRows.shuffled(random).take(tuning.emitters)
        chosenRows.forEach { row ->
            emitters.add(
                BoltEmitter(
                    lane = low + random.nextFloat() * (high - low),
                    y = row,
                    period = tuning.emitterPeriod,
                    phase = random.nextFloat()
                )
            )
        }

        relicSpots.shuffled(random).take(relicLeft).forEach { relics.add(it) }

        return FloorLayout(
            index = tuning.index,
            rows = rows,
            ledges = ledges,
            emitters = emitters,
            winds = winds,
            relics = relics,
            gateX = topX,
            boss = tuning.boss,
            seed = seed
        )
    }

    private fun fallback(tuning: FloorTuning, seed: Long): FloorLayout {
        val rows = tuning.rows
        val reach = GameConfig.DASH_REACH_LANES
        val ledges = mutableListOf(Ledge(3.5f, 0f, 3.2f))
        var y = 0f
        var x = 3.5f
        var toRight = true
        while (y + 4f <= rows) {
            y += 2f
            x = if (toRight) x + reach else x - reach
            if (x > GameConfig.LANES - GameConfig.LANE_MARGIN || x < GameConfig.LANE_MARGIN) {
                toRight = !toRight
                x = if (toRight) x + 2 * reach else x - 2 * reach
            }
            toRight = !toRight
            ledges.add(Ledge(x, y, 2.4f))
        }
        val topX = (x + reach).coerceAtMost(GameConfig.LANES - GameConfig.LANE_MARGIN)
        ledges.add(Ledge(topX, y + 2f, 2.6f))
        return FloorLayout(
            index = tuning.index,
            rows = rows,
            ledges = ledges,
            emitters = listOf(BoltEmitter(3.5f, 5f, tuning.emitterPeriod, 0.3f)),
            winds = listOf(WindColumn(3.5f, 6.35f, 1.3f, 4.8f)),
            relics = listOf(
                RelicAnchor(3.5f, 3.2f),
                RelicAnchor(2.6f, 7.2f),
                RelicAnchor(4.4f, 11.2f)
            ),
            gateX = topX,
            boss = tuning.boss,
            seed = seed
        )
    }

    private fun solvable(layout: FloorLayout): Boolean {
        if (layout.ledges.size < 3) return false
        val goal = layout.ledges.indices.maxByOrNull { layout.ledges[it].y } ?: return false
        val reached = BooleanArray(layout.ledges.size)
        reached[0] = true
        val queue = ArrayDeque<Int>()
        queue.add(0)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val from = layout.ledges[current]
            layout.ledges.forEachIndexed { index, target ->
                if (!reached[index] && canReach(from, target, layout)) {
                    reached[index] = true
                    queue.add(index)
                }
            }
        }
        if (!reached[goal]) return false
        val top = layout.ledges[goal]
        return abs(top.x - layout.gateX) < 1f && layout.gateY - top.y <= GameConfig.MAX_DASH_REACH_ROWS
    }

    private fun canReach(from: Ledge, target: Ledge, layout: FloorLayout): Boolean {
        val climb = target.y - from.y
        val gap = abs(target.x - from.x)
        if (climb <= 0f) return gap <= 3.4f && climb > -5f
        if (climb <= GameConfig.MAX_DASH_REACH_ROWS &&
            gap <= GameConfig.MAX_DASH_REACH_LANES
        ) {
            return true
        }
        return layout.winds.any { wind ->
            abs(wind.x - from.x) < wind.widthLanes / 2f + from.widthLanes / 2f + 0.8f &&
                from.y >= wind.y - 1.5f &&
                target.y <= wind.y + wind.heightRows + GameConfig.MAX_DASH_REACH_ROWS &&
                gap <= 3.2f
        }
    }
}
