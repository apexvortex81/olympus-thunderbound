package com.Fowvite0.Koqfam.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.di.RunArgs
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BoltIndigo
import com.Fowvite0.Koqfam.core.theme.BoltIndigoLight
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.DangerCrimson
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.SurfaceRaised
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.ui.PrimaryButton
import com.Fowvite0.Koqfam.core.ui.SecondaryButton
import com.Fowvite0.Koqfam.core.ui.StatEntry
import com.Fowvite0.Koqfam.core.ui.StatRow
import com.Fowvite0.Koqfam.core.ui.StormBackdrop
import com.Fowvite0.Koqfam.domain.model.Outcome

private val ResultOverlay = listOf(
    Color(0xF2050510),
    Color(0xE60A0A20),
    Color(0xF2050510)
)

@Composable
fun GameOverRoute(
    outcome: Outcome,
    heightMeters: Int,
    relics: Int,
    floorIndex: Int,
    customSlot: Int,
    onAgain: () -> Unit,
    onNextFloor: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: GameOverViewModel = viewModel(
        key = "result:$floorIndex:$customSlot:$heightMeters:$relics:${outcome.name}",
        factory = ServiceLocator.factory(
            RunArgs(
                floorIndex = floorIndex,
                customSlot = customSlot,
                outcome = outcome,
                heightMeters = heightMeters,
                relics = relics
            )
        )
    )
    val state by resultViewModel.state.collectAsStateWithLifecycle()

    GameOverScreen(
        state = state,
        onAgain = onAgain,
        onNextFloor = onNextFloor,
        onMenu = onMenu
    )
}

@Composable
fun GameOverScreen(
    state: GameOverUiState,
    onAgain: () -> Unit,
    onNextFloor: (Int) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        revealed = true
    }

    val cardScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.9f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "card-scale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "card-alpha"
    )
    val countedHeight by animateIntAsState(
        targetValue = if (revealed) state.heightMeters else 0,
        animationSpec = tween(durationMillis = 600),
        label = "height-count"
    )
    val countedScore by animateIntAsState(
        targetValue = if (revealed) state.score else 0,
        animationSpec = tween(durationMillis = 600),
        label = "score-count"
    )

    val outcomeRes = when (state.outcome) {
        Outcome.AEGIS -> R.string.outcome_aegis
        Outcome.SENTINEL -> R.string.outcome_sentinel
        Outcome.STORM -> R.string.outcome_storm
    }
    val outcomeColor = if (state.outcome == Outcome.STORM) DangerCrimson else CeremonialGold

    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(overlay = ResultOverlay, artScale = 1.08f)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceRaised,
                border = BorderStroke(1.dp, GoldHairline),
                shadowElevation = 20.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(cardScale)
                    .alpha(cardAlpha)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = stringResource(outcomeRes),
                        style = MaterialTheme.typography.displaySmall,
                        color = outcomeColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StatRow(
                        entries = listOf(
                            StatEntry(
                                labelRes = R.string.stat_height,
                                value = stringResource(R.string.height_value, countedHeight),
                                accent = BoltIndigo,
                                visible = true
                            ),
                            StatEntry(
                                labelRes = R.string.stat_relics,
                                value = stringResource(R.string.plain_number, state.relics),
                                accent = CeremonialGold,
                                visible = state.relics > 0
                            ),
                            StatEntry(
                                labelRes = R.string.stat_best,
                                value = stringResource(
                                    R.string.height_value,
                                    state.bestHeightMeters
                                ),
                                accent = BoltIndigoLight,
                                visible = state.bestHeightMeters > 0
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.stat_score),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = stringResource(R.string.plain_number, countedScore),
                        style = MaterialTheme.typography.headlineMedium,
                        color = BoltIndigoLight
                    )

                    if (state.isNewBest) {
                        Spacer(modifier = Modifier.height(14.dp))
                        NewBestRibbon()
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (state.nextFloor > 0) {
                PrimaryButton(
                    textRes = R.string.next_floor,
                    iconRes = R.drawable.ic_bolt,
                    gold = true,
                    onClick = { onNextFloor(state.nextFloor) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    textRes = R.string.play_again,
                    onClick = onAgain,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                PrimaryButton(
                    textRes = R.string.play_again,
                    iconRes = R.drawable.ic_bolt,
                    onClick = onAgain
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NewBestRibbon() {
    val transition = rememberInfiniteTransition(label = "ribbon")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ribbon-sweep"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0x00E8C06A),
                        Color(0xCCE8C06A),
                        Color(0x00E8C06A)
                    ),
                    startX = (sweep - 0.5f) * 600f,
                    endX = (sweep + 0.5f) * 600f
                ),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = stringResource(R.string.new_best),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF050510)
        )
    }
}
