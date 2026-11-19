package com.Fowvite0.Koqfam.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.core.di.RunArgs
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BoltIndigo
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.GlassSurface
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.IndigoHairline
import com.Fowvite0.Koqfam.core.theme.SurfaceRaised
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.theme.TextTertiary
import com.Fowvite0.Koqfam.core.ui.ChargeStrip
import com.Fowvite0.Koqfam.core.ui.PrimaryButton
import com.Fowvite0.Koqfam.core.ui.StormBackdrop
import com.Fowvite0.Koqfam.domain.model.Outcome
import com.Fowvite0.Koqfam.presentation.dialog.PauseDialog

private val GameOverlay = listOf(
    Color(0x99050510),
    Color(0x660A0A20),
    Color(0xCC050510)
)

@Composable
fun GameRoute(
    floorIndex: Int,
    customSlot: Int,
    attempt: Int,
    onFinished: (Outcome, Int, Int) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game:$floorIndex:$customSlot:$attempt",
        factory = ServiceLocator.factory(
            RunArgs(floorIndex = floorIndex, customSlot = customSlot, attempt = attempt)
        )
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(gameViewModel) {
        gameViewModel.events.collect { result ->
            onFinished(result.outcome, result.heightMeters, result.relics)
        }
    }

    DisposableEffect(gameViewModel) {
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onBegin = gameViewModel::begin,
        onDash = gameViewModel::dash,
        onAegis = gameViewModel::raiseAegis,
        onPause = gameViewModel::pause,
        onResume = gameViewModel::resume,
        onMenu = onMenu
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onBegin: () -> Unit,
    onDash: (Boolean) -> Unit,
    onAegis: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(
            overlay = GameOverlay,
            parallaxPx = -state.climb.cameraY * 1.8f,
            artScale = 1.18f
        )

        ShaftCanvas(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = state.idle, onClick = onBegin)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHud(
                heightMeters = state.heightMeters,
                floorIndex = state.layout?.index ?: state.floorIndex,
                relics = state.climb.relicsFound,
                onPause = onPause
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 26.dp)
        ) {
            if (state.showHint) {
                Text(
                    text = stringResource(
                        if (state.idle) R.string.hint_idle else R.string.hint_controls
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = BoltWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0xB3050510), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xE60A0A20),
                border = BorderStroke(1.dp, IndigoHairline),
                shadowElevation = 14.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    if (state.idle) {
                        PrimaryButton(
                            textRes = R.string.go,
                            iconRes = R.drawable.ic_bolt,
                            onClick = onBegin
                        )
                    } else {
                        ChargeStrip(
                            charges = state.climb.charges,
                            maxCharges = GameConfig.MAX_CHARGES,
                            partial = state.chargeFill
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ControlRow(
                            state = state,
                            onDash = onDash,
                            onAegis = onAegis
                        )
                    }
                }
            }
        }

        if (state.paused) {
            PauseDialog(
                heightMeters = state.heightMeters,
                relics = state.climb.relicsFound,
                seed = state.layout?.seed ?: 0L,
                onResume = onResume,
                onMenu = onMenu
            )
        }
    }
}

@Composable
private fun GameHud(
    heightMeters: Int,
    floorIndex: Int,
    relics: Int,
    onPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = GlassSurface,
            border = BorderStroke(1.dp, IndigoHairline),
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable(onClick = onPause)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_pause),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = GlassSurface,
            border = BorderStroke(1.dp, IndigoHairline)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.height_value, heightMeters),
                    style = MaterialTheme.typography.titleMedium,
                    color = BoltWhite,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Text(
                    text = stringResource(R.string.floor_value, floorIndex),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = GlassSurface,
            border = BorderStroke(1.dp, GoldHairline),
            modifier = Modifier.height(48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_relic),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.relic_fraction, relics),
                    style = MaterialTheme.typography.titleSmall,
                    color = CeremonialGold
                )
            }
        }
    }
}

@Composable
private fun ControlRow(
    state: GameUiState,
    onDash: (Boolean) -> Unit,
    onAegis: () -> Unit
) {
    val hasCharge = state.climb.charges > 0 && state.running
    val canShield = state.climb.bubbles > 0 && !state.shieldUp && state.running
    val leftGoesRight = state.swapDashSides
    val noChargeLabel = stringResource(R.string.sd_no_charge)
    val aegisLabel = stringResource(R.string.sd_aegis_active)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DashButton(
            textRes = R.string.dash_left,
            enabled = hasCharge,
            stateLabel = if (hasCharge) null else noChargeLabel,
            onClick = { onDash(leftGoesRight) },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onAegis,
            enabled = canShield,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceRaised,
                contentColor = CeremonialGold,
                disabledContainerColor = Color(0x660A0A20),
                disabledContentColor = TextTertiary
            ),
            border = BorderStroke(1.dp, GoldHairline),
            modifier = Modifier
                .weight(1.2f)
                .height(58.dp)
                .semantics { stateDescription = if (canShield) "" else aegisLabel }
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = stringResource(R.string.cd_shield),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.shield),
                style = MaterialTheme.typography.labelSmall,
                color = if (canShield) CeremonialGold else TextTertiary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.bubble_count, state.climb.bubbles),
                style = MaterialTheme.typography.labelSmall,
                color = if (canShield) BoltWhite else TextTertiary
            )
        }

        DashButton(
            textRes = R.string.dash_right,
            enabled = hasCharge,
            stateLabel = if (hasCharge) null else noChargeLabel,
            onClick = { onDash(!leftGoesRight) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DashButton(
    textRes: Int,
    enabled: Boolean,
    stateLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BoltIndigo,
            contentColor = BoltWhite,
            disabledContainerColor = Color(0x664169E1),
            disabledContentColor = TextTertiary
        ),
        border = BorderStroke(1.dp, if (enabled) IndigoHairline else Color(0x332A2C3E)),
        modifier = modifier
            .height(58.dp)
            .semantics { stateDescription = stateLabel ?: "" }
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) BoltWhite else TextTertiary,
            maxLines = 1
        )
    }
}

