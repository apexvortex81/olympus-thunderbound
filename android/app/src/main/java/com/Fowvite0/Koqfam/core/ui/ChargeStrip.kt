package com.Fowvite0.Koqfam.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.Fowvite0.Koqfam.core.theme.BasaltStone
import com.Fowvite0.Koqfam.core.theme.BoltIndigoLight
import com.Fowvite0.Koqfam.core.theme.BoltWhite

@Composable
fun ChargeStrip(
    charges: Int,
    maxCharges: Int,
    partial: Float,
    modifier: Modifier = Modifier
) {
    val fill by animateFloatAsState(
        targetValue = partial.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 160),
        label = "charge-fill"
    )
    Row(
        modifier = modifier.height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxCharges) { index ->
            val level = when {
                index < charges -> 1f
                index == charges -> fill
                else -> 0f
            }
            Canvas(modifier = Modifier.size(10.dp)) {
                val radius = size.minDimension / 2f
                drawCircle(color = BasaltStone, radius = radius)
                if (level > 0f) {
                    drawCircle(
                        color = if (level >= 1f) BoltWhite else BoltIndigoLight,
                        radius = radius * level.coerceIn(0.25f, 1f)
                    )
                }
            }
        }
    }
}
