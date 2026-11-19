package com.Fowvite0.Koqfam.presentation.howto

import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BasaltStone
import com.Fowvite0.Koqfam.core.theme.BoltIndigo
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.IndigoHairline
import com.Fowvite0.Koqfam.core.theme.SurfaceRaised
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.ui.GlassCard
import com.Fowvite0.Koqfam.core.ui.SecondaryButton
import com.Fowvite0.Koqfam.core.ui.SectionHeader
import com.Fowvite0.Koqfam.core.ui.StormBackdrop
import androidx.compose.ui.graphics.Color

private val HowToOverlay = listOf(
    Color(0xE6050510),
    Color(0xF20A0A20),
    Color(0xF2050510)
)

@Composable
fun HowToPlayRoute(onBack: () -> Unit) {
    val howToViewModel: HowToPlayViewModel =
        viewModel(key = "howto", factory = ServiceLocator.factory())
    val state by howToViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        howToViewModel.refresh()
    }

    HowToPlayScreen(
        state = state,
        onTelegraph = howToViewModel::toggleTelegraph,
        onSwapSides = howToViewModel::toggleSwapSides,
        onBack = onBack
    )
}

@Composable
fun HowToPlayScreen(
    state: HowToPlayUiState,
    onTelegraph: (Boolean) -> Unit,
    onSwapSides: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(overlay = HowToOverlay, artScale = 1.1f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.howto_title),
                style = MaterialTheme.typography.headlineMedium,
                color = BoltWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
            ControlCard(R.string.howto_1_title, R.string.howto_1_body)
            Spacer(modifier = Modifier.height(8.dp))
            ControlCard(R.string.howto_2_title, R.string.howto_2_body)
            Spacer(modifier = Modifier.height(8.dp))
            ControlCard(R.string.howto_3_title, R.string.howto_3_body)
            Spacer(modifier = Modifier.height(8.dp))
            ControlCard(R.string.howto_4_title, R.string.howto_4_body)

            Spacer(modifier = Modifier.height(14.dp))

            SectionHeader(titleRes = R.string.howto_legend)

            Spacer(modifier = Modifier.height(8.dp))

            LegendRow(R.drawable.sprite_3, R.string.cd_bolt_orb, R.string.legend_hazard)
            LegendRow(R.drawable.sprite_2, R.string.cd_basalt_slab, R.string.legend_slab)
            LegendRow(R.drawable.sprite_4, R.string.cd_relic, R.string.legend_relic)
            LegendRow(R.drawable.sprite_5, R.string.cd_shield, R.string.legend_aegis)

            Spacer(modifier = Modifier.height(14.dp))

            SectionHeader(titleRes = R.string.howto_assists)

            Spacer(modifier = Modifier.height(8.dp))

            AssistRow(
                titleRes = R.string.assist_telegraph,
                bodyRes = R.string.assist_telegraph_body,
                checked = state.telegraphAssist,
                onChange = onTelegraph
            )
            Spacer(modifier = Modifier.height(8.dp))
            AssistRow(
                titleRes = R.string.assist_swap,
                bodyRes = R.string.assist_swap_body,
                checked = state.swapDashSides,
                onChange = onSwapSides
            )

            }

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                textRes = R.string.back,
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ControlCard(titleRes: Int, bodyRes: Int) {
    GlassCard(
        borderColor = IndigoHairline,
        contentPadding = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            color = CeremonialGold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(bodyRes),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun LegendRow(drawableRes: Int, descriptionRes: Int, textRes: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = stringResource(descriptionRes),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun AssistRow(
    titleRes: Int,
    bodyRes: Int,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val onLabel = stringResource(R.string.sd_assist_on)
    val offLabel = stringResource(R.string.sd_assist_off)
    GlassCard(
        borderColor = GoldHairline,
        contentPadding = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = BoltWhite
                )
                Text(
                    text = stringResource(bodyRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BoltWhite,
                    checkedTrackColor = BoltIndigo,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceRaised,
                    uncheckedBorderColor = BasaltStone
                ),
                modifier = Modifier.semantics {
                    stateDescription = if (checked) onLabel else offLabel
                }
            )
        }
    }
}
