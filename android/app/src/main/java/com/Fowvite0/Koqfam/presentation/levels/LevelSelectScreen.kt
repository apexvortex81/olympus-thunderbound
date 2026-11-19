package com.Fowvite0.Koqfam.presentation.levels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BasaltStone
import com.Fowvite0.Koqfam.core.theme.BoltIndigoLight
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.IndigoHairline
import com.Fowvite0.Koqfam.core.theme.RaisedGlass
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.theme.TextTertiary
import com.Fowvite0.Koqfam.core.ui.EmptyStateMessage
import com.Fowvite0.Koqfam.core.ui.GlassCard
import com.Fowvite0.Koqfam.core.ui.SecondaryButton
import com.Fowvite0.Koqfam.core.ui.SectionHeader
import com.Fowvite0.Koqfam.core.ui.StormBackdrop
import com.Fowvite0.Koqfam.domain.model.FloorTile

private val LevelsOverlay = listOf(
    Color(0xE6050510),
    Color(0xF20A0A20),
    Color(0xF2050510)
)

@Composable
fun LevelSelectRoute(
    onFloor: (Int) -> Unit,
    onTemple: (Int) -> Unit,
    onEditor: () -> Unit,
    onBack: () -> Unit
) {
    val levelsViewModel: LevelSelectViewModel =
        viewModel(key = "levels", factory = ServiceLocator.factory())
    val state by levelsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        levelsViewModel.refresh()
    }

    LevelSelectScreen(
        state = state,
        onFloor = onFloor,
        onTemple = onTemple,
        onEditor = onEditor,
        onBack = onBack
    )
}

@Composable
fun LevelSelectScreen(
    state: LevelSelectUiState,
    onFloor: (Int) -> Unit,
    onTemple: (Int) -> Unit,
    onEditor: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(overlay = LevelsOverlay, artScale = 1.1f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.levels_title),
                style = MaterialTheme.typography.headlineMedium,
                color = BoltWhite
            )

            Spacer(modifier = Modifier.height(14.dp))

            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (column in 0 until 3) {
                        val index = row * 3 + column
                        val tile = state.floors.getOrNull(index)
                        if (tile == null) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            FloorTileCard(
                                tile = tile,
                                onClick = { onFloor(tile.index) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            SectionHeader(titleRes = R.string.your_temples)

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (state.temples.isEmpty()) {
                    EmptyStateMessage(messageRes = R.string.no_temples)
                } else {
                    state.temples.forEach { temple ->
                    GlassCard(
                        borderColor = IndigoHairline,
                        contentPadding = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(
                                        R.string.temple_slot,
                                        temple.slot + 1
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = BoltWhite
                                )
                                Text(
                                    text = stringResource(
                                        R.string.piece_count_short,
                                        temple.pieceCount
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            SecondaryButton(
                                textRes = R.string.play,
                                onClick = { onTemple(temple.slot) },
                                gold = true,
                                modifier = Modifier.width(110.dp)
                            )
                        }
                    }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    textRes = R.string.level_editor,
                    onClick = onEditor,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    textRes = R.string.back,
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun FloorTileCard(
    tile: FloorTile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lockedLabel = stringResource(R.string.sd_locked)
    val unlockedLabel = stringResource(R.string.sd_unlocked)
    val border = when {
        !tile.unlocked -> BorderStroke(1.dp, BasaltStone)
        tile.boss -> BorderStroke(1.dp, GoldHairline)
        else -> BorderStroke(1.dp, IndigoHairline)
    }
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (tile.unlocked) RaisedGlass else Color(0x990A0A20),
        border = border,
        shadowElevation = if (tile.unlocked) 10.dp else 0.dp,
        modifier = modifier
            .height(96.dp)
            .semantics { stateDescription = if (tile.unlocked) unlockedLabel else lockedLabel }
            .clickable(enabled = tile.unlocked, onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (tile.boss) {
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                if (!tile.unlocked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = stringResource(R.string.floor_value, tile.index),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (tile.unlocked) BoltWhite else TextTertiary,
                    textAlign = TextAlign.Center
                )
                if (tile.boss) {
                    Text(
                        text = stringResource(R.string.sentinel_arena),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (tile.unlocked) CeremonialGold else TextTertiary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
                if (tile.unlocked && tile.bestHeightMeters > 0) {
                    Text(
                        text = stringResource(R.string.height_value, tile.bestHeightMeters),
                        style = MaterialTheme.typography.labelSmall,
                        color = BoltIndigoLight
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { dot ->
                        Spacer(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (dot < tile.relicsFound) {
                                        CeremonialGold
                                    } else {
                                        BasaltStone
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}
