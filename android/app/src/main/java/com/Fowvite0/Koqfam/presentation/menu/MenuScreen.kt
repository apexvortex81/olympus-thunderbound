package com.Fowvite0.Koqfam.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BoltIndigo
import com.Fowvite0.Koqfam.core.theme.BoltIndigoLight
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.IndigoHairline
import com.Fowvite0.Koqfam.core.theme.RaisedGlass
import com.Fowvite0.Koqfam.core.theme.SheetSurface
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.theme.TextTertiary
import com.Fowvite0.Koqfam.core.ui.EmptyStateMessage
import com.Fowvite0.Koqfam.core.ui.PrimaryButton
import com.Fowvite0.Koqfam.core.ui.SecondaryButton
import com.Fowvite0.Koqfam.core.ui.StatEntry
import com.Fowvite0.Koqfam.core.ui.StatRow
import com.Fowvite0.Koqfam.core.ui.StormBackdrop

private val MenuOverlay = listOf(
    Color(0x00050510),
    Color(0x66050510),
    Color(0xE60A0A20)
)

@Composable
fun MenuRoute(
    onPlay: (Int) -> Unit,
    onLevels: () -> Unit,
    onEditor: () -> Unit,
    onHowTo: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(key = "menu", factory = ServiceLocator.factory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuScreen(
        state = state,
        onPlay = { onPlay(state.currentFloor) },
        onLevels = onLevels,
        onEditor = onEditor,
        onHowTo = onHowTo
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onEditor: () -> Unit,
    onHowTo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        revealed = true
    }

    val sheetOffset by animateFloatAsState(
        targetValue = if (revealed) 0f else 36f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "sheet-rise"
    )
    val sheetAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 420),
        label = "sheet-fade"
    )

    val transition = rememberInfiniteTransition(label = "menu-loop")
    val heroFloat by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero-float"
    )

    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(overlay = MenuOverlay, artScale = 1.04f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            MenuTopStrip(relics = state.relicsTotal)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 6.dp)
                        .size(190.dp)
                ) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x3DE8C06A), Color.Transparent),
                            radius = size.minDimension / 2f
                        ),
                        topLeft = Offset(0f, size.height * 0.2f),
                        size = Size(size.width, size.height * 0.7f)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_messenger),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 26.dp, bottom = 10.dp)
                        .height(128.dp)
                        .offset { IntOffset(0, heroFloat.toInt()) }
                )
            }

            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = SheetSurface,
                border = BorderStroke(1.dp, IndigoHairline),
                shadowElevation = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(sheetAlpha)
                    .offset { IntOffset(0, sheetOffset.toInt()) }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.menu_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = BoltWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.menu_tagline,
                            state.currentTemple,
                            state.currentFloor
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val entries = listOf(
                        StatEntry(
                            labelRes = R.string.stat_best_height,
                            value = stringResource(R.string.height_value, state.bestHeightMeters),
                            accent = BoltIndigo,
                            visible = state.bestHeightMeters > 0
                        ),
                        StatEntry(
                            labelRes = R.string.stat_relics,
                            value = stringResource(R.string.plain_number, state.relicsTotal),
                            accent = CeremonialGold,
                            visible = state.relicsTotal > 0
                        ),
                        StatEntry(
                            labelRes = R.string.stat_temples,
                            value = stringResource(R.string.plain_number, state.templesCleared),
                            accent = BoltIndigoLight,
                            visible = state.templesCleared > 0
                        )
                    )
                    if (entries.count { it.visible } >= 2) {
                        StatRow(entries = entries)
                    } else {
                        EmptyStateMessage(messageRes = R.string.first_ascent)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.menu_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        textRes = R.string.play,
                        iconRes = R.drawable.ic_bolt,
                        onClick = onPlay
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryButton(
                            textRes = R.string.levels,
                            onClick = onLevels,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            textRes = R.string.how_to_play,
                            onClick = onHowTo,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryButton(
                        textRes = R.string.level_editor,
                        onClick = onEditor,
                        gold = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.legal_line),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuTopStrip(relics: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            shape = CircleShape,
            color = RaisedGlass,
            border = BorderStroke(1.dp, GoldHairline),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = stringResource(R.string.cd_temple_gate),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = RaisedGlass,
            border = BorderStroke(1.dp, GoldHairline),
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_relic),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.plain_number, relics),
                    style = MaterialTheme.typography.titleSmall,
                    color = CeremonialGold
                )
            }
        }
    }
}
