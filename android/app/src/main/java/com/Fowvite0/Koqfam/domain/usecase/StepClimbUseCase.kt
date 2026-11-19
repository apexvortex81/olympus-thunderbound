package com.Fowvite0.Koqfam.domain.usecase

import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.ClimbState
import com.Fowvite0.Koqfam.domain.model.FloorLayout
import com.Fowvite0.Koqfam.domain.model.Outcome
import com.Fowvite0.Koqfam.domain.model.RunPhase
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

class StepClimbUseCase {

    operator fun invoke(
        state: ClimbState,
        layout: FloorLayout,
        assistTelegraph: Boolean,
        dt: Float = GameConfig.TICK_SECONDS
    ): ClimbState {
        if (state.phase == RunPhase.ENDING) return advanceEnding(state, dt)
        if (state.phase != RunPhase.RUNNING) return state

        val time = state.runTime + dt
        var heroX = state.heroX
        var heroY = state.heroY
        var vx = state.velocityX
        var vy = state.velocityY
        var grounded = state.grounded
        var ledgeIndex = state.ledgeIndex
        var charges = state.charges
        var chargeTimer = state.chargeTimer
        var bubbles = state.bubbles
        var shield = max(0f, state.shieldRemaining - dt)
        val trail = max(0f, state.dashTrail - dt)
        val flash = max(0f, state.flash - dt)

        val inWind = layout.winds.any { wind ->
            heroX > wind.x - wind.widthLanes / 2f &&
                heroX < wind.x + wind.widthLanes / 2f &&
                heroY > wind.y &&
                heroY < wind.y + wind.heightRows
        }

        if (inWind) {
            grounded = false
            vy = min(GameConfig.WIND_MAX_VY, vy + GameConfig.WIND_ACCEL * dt)
            charges = GameConfig.MAX_CHARGES
            chargeTimer = GameConfig.CHARGE_REFILL_SECONDS
        }

        if (charges < GameConfig.MAX_CHARGES && !inWind) {
            chargeTimer -= dt
            if (chargeTimer <= 0f) {
                charges += 1
                chargeTimer = GameConfig.CHARGE_REFILL_SECONDS
            }
        }

        if (grounded && ledgeIndex in layout.ledges.indices) {
            val ledge = layout.ledges[ledgeIndex]
            val now = ledge.xAt(time)
            val before = ledge.xAt(state.runTime)
            heroX += now - before
            heroY = ledge.y
            vy = 0f
            vx *= 0.6f
            val settle = GameConfig.FOOTING_SPEED * dt
            heroX += (now - heroX).coerceIn(-settle, settle)
            if (abs(heroX - now) > ledge.widthLanes / 2f + GameConfig.HERO_WIDTH * 0.5f) {
                grounded = false
            }
        } else {
            grounded = false
            if (!inWind) {
                vy = max(-GameConfig.MAX_FALL, vy - GameConfig.GRAVITY * dt)
            }
            val nextX = heroX + vx * dt
            val nextY = heroY + vy * dt
            if (vy < 0f) {
                val landing = findLanding(layout, heroX, heroY, nextX, nextY, time)
                if (landing >= 0) {
                    ledgeIndex = landing
                    grounded = true
                    heroY = layout.ledges[landing].y
                    heroX = nextX
                    vy = 0f
                } else {
                    heroX = nextX
                    heroY = nextY
                }
            } else {
                heroX = nextX
                heroY = nextY
            }
            vx *= GameConfig.AIR_DRAG
        }

        val half = GameConfig.HERO_WIDTH / 2f
        if (heroX < half) {
            heroX = half
            vx = 0f
        }
        val rightWall = GameConfig.LANES - half
        if (heroX > rightWall) {
            heroX = rightWall
            vx = 0f
        }

        val facingRight = when {
            vx > 0.4f -> true
            vx < -0.4f -> false
            else -> state.facingRight
        }

        val relicTaken = state.relicTaken.toMutableList()
        var relicsFound = state.relicsFound
        layout.relics.forEachIndexed { index, anchor ->
            if (index < relicTaken.size && !relicTaken[index]) {
                val distance = hypot(
                    heroX - anchor.x,
                    heroY + GameConfig.HERO_HEIGHT / 2f - anchor.y
                )
                if (distance < GameConfig.RELIC_RADIUS + GameConfig.HERO_WIDTH / 2f) {
                    relicTaken[index] = true
                    relicsFound += 1
                    bubbles = min(GameConfig.MAX_BUBBLES, bubbles + 1)
                }
            }
        }

        val tideY = GameConfig.TIDE_BEGIN_ROWS + time * GameConfig.TIDE_RISE_ROWS
        val maxRows = max(state.maxRows, heroY)

        var sentinelY = state.sentinelY
        var sentinelHits = state.sentinelHits
        var sentinelCooldown = max(0f, state.sentinelCooldown - dt)
        var winGlow = state.winGlow
        var outcome: Outcome? = null

        if (layout.boss) {
            val coreDistance = hypot(heroX - layout.gateX, heroY - sentinelY)
            if (coreDistance < GameConfig.SENTINEL_CORE_RADIUS && sentinelCooldown <= 0f) {
                sentinelHits += 1
                sentinelCooldown = GameConfig.SENTINEL_COOLDOWN
                sentinelY = min(layout.rows + 1.4f, sentinelY + 1f)
                vy = -6f
                grounded = false
                if (sentinelHits >= GameConfig.SENTINEL_HITS_TO_CLEAR) {
                    outcome = Outcome.SENTINEL
                    winGlow = 1f
                }
            }
            if (outcome == null && shield <= 0f && sentinelCooldown <= 0f &&
                ringHit(time, layout.gateX, sentinelY, heroX, heroY)
            ) {
                outcome = Outcome.STORM
            }
        } else if (heroY >= layout.gateY - 0.65f && abs(heroX - layout.gateX) < 1.6f) {
            outcome = Outcome.AEGIS
            winGlow = 1f
        } else if (heroY >= layout.gateY + 1.2f) {
            outcome = Outcome.AEGIS
            winGlow = 1f
        }

        if (outcome == null && shield <= 0f && struckByBolt(layout, time, heroX, heroY, assistTelegraph)) {
            outcome = Outcome.STORM
        }
        if (outcome == null && heroY < tideY) {
            outcome = Outcome.STORM
        }

        val cameraTarget = max(heroY + GameConfig.CAMERA_LEAD, GameConfig.VISIBLE_ROWS * 0.42f)
        val cameraY = state.cameraY + (cameraTarget - state.cameraY) * GameConfig.CAMERA_LERP

        if (outcome == null && heroY < cameraY - GameConfig.DEATH_DROP_ROWS) {
            outcome = Outcome.STORM
        }

        val next = state.copy(
            heroX = heroX,
            heroY = heroY,
            velocityX = vx,
            velocityY = vy,
            grounded = grounded,
            ledgeIndex = ledgeIndex,
            facingRight = facingRight,
            cameraY = cameraY,
            runTime = time,
            charges = charges,
            chargeTimer = chargeTimer,
            bubbles = bubbles,
            shieldRemaining = shield,
            dashTrail = trail,
            relicTaken = relicTaken,
            relicsFound = relicsFound,
            tideY = tideY,
            maxRows = maxRows,
            sentinelY = sentinelY,
            sentinelHits = sentinelHits,
            sentinelCooldown = sentinelCooldown,
            flash = flash,
            winGlow = winGlow
        )

        return if (outcome != null) {
            next.copy(
                phase = RunPhase.ENDING,
                dissolve = GameConfig.DISSOLVE_SECONDS,
                flash = if (outcome == Outcome.STORM) GameConfig.FLASH_SECONDS else 0f,
                outcome = outcome
            )
        } else {
            next
        }
    }

    private fun advanceEnding(state: ClimbState, dt: Float): ClimbState {
        val left = state.dissolve - dt
        return if (left <= 0f) {
            state.copy(dissolve = 0f, phase = RunPhase.FINISHED)
        } else {
            state.copy(
                dissolve = left,
                flash = max(0f, state.flash - dt),
                runTime = state.runTime + dt
            )
        }
    }

    private fun findLanding(
        layout: FloorLayout,
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        time: Float
    ): Int {
        var best = -1
        var bestY = -1000f
        layout.ledges.forEachIndexed { index, ledge ->
            val top = ledge.y
            if (fromY >= top - 0.02f && toY <= top + 0.02f) {
                val centre = ledge.xAt(time)
                val span = ledge.widthLanes / 2f + GameConfig.LANDING_SLACK
                val crossX = if (abs(toX - fromX) < 0.0001f) toX else toX
                if (abs(crossX - centre) <= span && top > bestY) {
                    bestY = top
                    best = index
                }
            }
        }
        return best
    }

    private fun struckByBolt(
        layout: FloorLayout,
        time: Float,
        heroX: Float,
        heroY: Float,
        assist: Boolean
    ): Boolean {
        val telegraph = if (assist) {
            GameConfig.TELEGRAPH_ASSIST_SECONDS
        } else {
            GameConfig.TELEGRAPH_SECONDS
        }
        val centre = heroY + GameConfig.HERO_HEIGHT / 2f
        return layout.emitters.any { emitter ->
            val phase = emitter.phaseAt(time)
            val striking = phase >= emitter.period - GameConfig.STRIKE_SECONDS
            val inRow = abs(centre - emitter.y) < GameConfig.BEAM_HALF_HEIGHT
            val telegraphing = phase >= emitter.period - GameConfig.STRIKE_SECONDS - telegraph
            striking && inRow && telegraphing && abs(heroX - emitter.lane) < GameConfig.LANES.toFloat()
        }
    }

    private fun ringHit(
        time: Float,
        centreX: Float,
        centreY: Float,
        heroX: Float,
        heroY: Float
    ): Boolean {
        val radius = (time % GameConfig.SENTINEL_RING_PERIOD) * GameConfig.SENTINEL_RING_SPEED
        val distance = hypot(heroX - centreX, heroY - centreY)
        return abs(distance - radius) < GameConfig.SENTINEL_RING_BAND
    }
}
