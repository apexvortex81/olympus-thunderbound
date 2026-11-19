package com.Fowvite0.Koqfam.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.IndigoHairline
import com.Fowvite0.Koqfam.core.theme.RaisedGlass
import com.Fowvite0.Koqfam.core.theme.TextTertiary

@Composable
fun SecondaryButton(
    textRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gold: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium),
        label = "secondary-press"
    )
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 12.dp),
        border = BorderStroke(1.dp, if (gold) GoldHairline else IndigoHairline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = RaisedGlass,
            contentColor = if (gold) CeremonialGold else BoltWhite,
            disabledContainerColor = RaisedGlass,
            disabledContentColor = TextTertiary
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 50.dp)
            .height(50.dp)
            .scale(scale)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
