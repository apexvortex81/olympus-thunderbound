package com.Fowvite0.Koqfam.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.FloorLayout
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin

private val GoldLine = Color(0x66E8C06A)
private val GoldSolid = Color(0xFFE8C06A)
private val BoltLine = Color(0xFFEAF2FF)
private val WindTint = Color(0x3D4169E1)
private val TideViolet = Color(0xCC7B2FBE)
private val TideIndigo = Color(0xB34169E1)
private val TideFoam = Color(0x33EAF2FF)
private val CloudTint = Color(0x1AEAF2FF)

@Composable
fun ShaftCanvas(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    val layout = state.layout ?: return
    val hero = ImageBitmap.imageResource(R.drawable.sprite_1)
    val slab = ImageBitmap.imageResource(R.drawable.sprite_2)
    val orb = ImageBitmap.imageResource(R.drawable.sprite_3)
    val relic = ImageBitmap.imageResource(R.drawable.sprite_4)
    val aegis = ImageBitmap.imageResource(R.drawable.sprite_5)
    val climb = state.climb

    Canvas(modifier = modifier) {
        val frame = 10.dp.toPx()
        val cell = (size.width - frame * 2f) / GameConfig.LANES
        val centreY = size.height / 2f
        val time = climb.runTime
        val camera = climb.cameraY

        fun sx(worldX: Float): Float = frame + worldX * cell
        fun sy(worldY: Float): Float = centreY - (worldY - camera) * cell

        drawCloudBands(camera, cell, size.width, size.height)

        layout.winds.forEach { wind ->
            val left = sx(wind.x - wind.widthLanes / 2f)
            val top = sy(wind.y + wind.heightRows)
            val height = wind.heightRows * cell
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, WindTint, Color.Transparent),
                    startY = top,
                    endY = top + height
                ),
                topLeft = Offset(left, top),
                size = Size(wind.widthLanes * cell, height)
            )
            val step = height / 9f
            val rise = (time * 1.6f % 1f) * step
            for (i in 0 until 9) {
                val y = top + height - i * step - rise
                if (y < top || y > top + height) continue
                val chevron = Path().apply {
                    moveTo(left + cell * 0.15f, y)
                    lineTo(left + wind.widthLanes * cell / 2f, y - step * 0.35f)
                    lineTo(left + wind.widthLanes * cell - cell * 0.15f, y)
                }
                drawPath(chevron, color = Color(0x667B9BFF), style = Stroke(width = 2.5f))
            }
        }

        val bestRows = state.bestHeightMeters.toFloat() / GameConfig.METERS_PER_ROW
        if (bestRows > 1f && bestRows < layout.rows + 2f) {
            val y = sy(bestRows)
            drawLine(
                color = GoldLine,
                start = Offset(frame, y),
                end = Offset(size.width - frame, y),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f))
            )
        }

        layout.ledges.forEachIndexed { index, ledge ->
            val centre = ledge.xAt(time)
            val widthPx = ledge.widthLanes * cell
            val topY = sy(ledge.y)
            if (topY < -cell * 2f || topY > size.height + cell * 2f) return@forEachIndexed
            val tileHeight = cell * 0.42f
            val ratio = slab.width.toFloat() / slab.height.toFloat()
            val tileWidth = tileHeight * ratio
            val count = ceil(widthPx / tileWidth).toInt().coerceAtLeast(1)
            val leftPx = sx(centre) - widthPx / 2f
            for (i in 0 until count) {
                val cx = leftPx + tileWidth * (i + 0.5f)
                drawSprite(slab, cx, topY + tileHeight * 0.32f, tileHeight)
            }
            if (ledge.drifts) {
                drawLine(
                    color = GoldSolid,
                    start = Offset(leftPx, topY + tileHeight * 0.75f),
                    end = Offset(leftPx + widthPx, topY + tileHeight * 0.75f),
                    strokeWidth = 3f
                )
            }
            if (index == 0) {
                drawRect(
                    color = Color(0x332A2C3E),
                    topLeft = Offset(leftPx, topY + tileHeight * 0.75f),
                    size = Size(widthPx, cell * 1.4f)
                )
            }
        }

        val telegraph = if (state.telegraphAssist) {
            GameConfig.TELEGRAPH_ASSIST_SECONDS
        } else {
            GameConfig.TELEGRAPH_SECONDS
        }
        layout.emitters.forEach { emitter ->
            val phase = emitter.phaseAt(time)
            val strikeBegin = emitter.period - GameConfig.STRIKE_SECONDS
            val warnBegin = strikeBegin - telegraph
            val y = sy(emitter.y)
            if (phase >= strikeBegin) {
                drawLine(
                    color = BoltLine,
                    start = Offset(frame, y),
                    end = Offset(size.width - frame, y),
                    strokeWidth = 9f
                )
            } else if (phase >= warnBegin) {
                drawLine(
                    color = BoltLine.copy(alpha = 0.35f),
                    start = Offset(frame, y),
                    end = Offset(size.width - frame, y),
                    strokeWidth = 3f
                )
            }
            drawSprite(orb, sx(emitter.lane), y, cell * 0.52f)
        }

        layout.relics.forEachIndexed { index, anchor ->
            val taken = climb.relicTaken.getOrNull(index) ?: false
            if (taken) return@forEachIndexed
            val bob = sin(time * 4.5f + index) * cell * 0.08f
            drawSprite(relic, sx(anchor.x), sy(anchor.y) + bob, cell * 0.48f)
        }

        if (layout.boss) {
            val coreX = sx(layout.gateX)
            val coreY = sy(climb.sentinelY)
            val radius = (time % GameConfig.SENTINEL_RING_PERIOD) *
                GameConfig.SENTINEL_RING_SPEED * cell
            drawCircle(
                color = BoltLine.copy(alpha = 0.55f),
                radius = radius,
                center = Offset(coreX, coreY),
                style = Stroke(width = 6f)
            )
            drawSprite(orb, coreX, coreY, cell * 1.1f)
            drawCircle(
                color = GoldSolid.copy(alpha = 0.5f),
                radius = cell * 0.7f,
                center = Offset(coreX, coreY),
                style = Stroke(width = 3f)
            )
        } else {
            val gateX = sx(layout.gateX)
            val gateY = sy(layout.gateY)
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0x00E8C06A), Color(0x99E8C06A))
                ),
                topLeft = Offset(gateX - cell * 1.1f, gateY - cell * 0.1f),
                size = Size(cell * 2.2f, cell * 1.2f)
            )
            drawLine(
                color = GoldSolid,
                start = Offset(gateX - cell * 1.1f, gateY),
                end = Offset(gateX + cell * 1.1f, gateY),
                strokeWidth = 5f
            )
        }

        if (climb.dashTrail > 0f) {
            val alpha = (climb.dashTrail / GameConfig.DASH_TRAIL_SECONDS).coerceIn(0f, 1f)
            val hx = sx(climb.heroX)
            val hy = sy(climb.heroY + GameConfig.HERO_HEIGHT / 2f)
            drawLine(
                color = BoltLine.copy(alpha = alpha * 0.8f),
                start = Offset(hx - climb.velocityX * cell * 0.12f, hy + cell * 0.5f),
                end = Offset(hx, hy),
                strokeWidth = 5f
            )
        }

        val heroCx = sx(climb.heroX)
        val heroCy = sy(climb.heroY + GameConfig.HERO_HEIGHT / 2f)
        val heroAlpha = if (climb.dissolve > 0f) {
            (climb.dissolve / GameConfig.DISSOLVE_SECONDS).coerceIn(0f, 1f)
        } else {
            1f
        }
        drawSprite(
            image = hero,
            centreX = heroCx,
            centreY = heroCy,
            targetHeight = cell * GameConfig.HERO_HEIGHT,
            mirrored = !climb.facingRight,
            alpha = heroAlpha
        )

        if (climb.shieldRemaining > 0f) {
            drawSprite(aegis, heroCx, heroCy, cell * 1.3f, alpha = 0.8f)
            drawCircle(
                color = GoldSolid.copy(alpha = 0.75f),
                radius = cell * 0.68f,
                center = Offset(heroCx, heroCy),
                style = Stroke(width = 3f)
            )
        }

        val tideTop = sy(climb.tideY)
        if (tideTop < size.height) {
            drawTide(tideTop, size.width, size.height, time)
        }

        if (climb.flash > 0f) {
            drawRect(
                color = BoltLine.copy(
                    alpha = (climb.flash / GameConfig.FLASH_SECONDS).coerceIn(0f, 1f) * 0.6f
                )
            )
        }

        if (climb.winGlow > 0f) {
            val target = if (layout.boss) climb.sentinelY else layout.gateY
            drawCircle(
                color = GoldSolid.copy(alpha = 0.45f),
                radius = cell * 2.4f,
                center = Offset(sx(layout.gateX), sy(target)),
                style = Stroke(width = 8f)
            )
        }
    }
}

private fun DrawScope.drawCloudBands(camera: Float, cell: Float, width: Float, height: Float) {
    val slow = (camera * 0.25f * cell) % (height / 2f)
    val fast = (camera * 0.45f * cell) % (height / 2f)
    drawRect(
        color = CloudTint,
        topLeft = Offset(0f, slow),
        size = Size(width, height * 0.06f)
    )
    drawRect(
        color = CloudTint.copy(alpha = 0.16f),
        topLeft = Offset(0f, fast + height * 0.35f),
        size = Size(width, height * 0.09f)
    )
}

private fun DrawScope.drawTide(top: Float, width: Float, height: Float, time: Float) {
    val bands = listOf(TideFoam, TideIndigo, TideViolet)
    bands.forEachIndexed { index, color ->
        val path = Path()
        val offset = index * 10f
        path.moveTo(0f, height)
        path.lineTo(0f, top + offset)
        var x = 0f
        while (x <= width) {
            val wave = sin((x / width) * 6.28f * 1.6f + time * 1.4f + index) * 9f
            path.lineTo(x, top + offset + wave)
            x += 12f
        }
        path.lineTo(width, height)
        path.close()
        drawPath(path, color = color)
    }
}

private fun DrawScope.drawSprite(
    image: ImageBitmap,
    centreX: Float,
    centreY: Float,
    targetHeight: Float,
    mirrored: Boolean = false,
    alpha: Float = 1f
) {
    if (targetHeight <= 0f || alpha <= 0.01f) return
    val ratio = image.width.toFloat() / image.height.toFloat()
    val drawHeight = targetHeight
    val drawWidth = targetHeight * ratio
    val left = centreX - drawWidth / 2f
    val top = centreY - drawHeight / 2f
    if (abs(drawWidth) < 1f || abs(drawHeight) < 1f) return
    if (mirrored) {
        scale(scaleX = -1f, scaleY = 1f, pivot = Offset(centreX, centreY)) {
            drawImage(
                image = image,
                dstOffset = IntOffset(left.toInt(), top.toInt()),
                dstSize = IntSize(drawWidth.toInt(), drawHeight.toInt()),
                alpha = alpha
            )
        }
    } else {
        drawImage(
            image = image,
            dstOffset = IntOffset(left.toInt(), top.toInt()),
            dstSize = IntSize(drawWidth.toInt(), drawHeight.toInt()),
            alpha = alpha
        )
    }
}

