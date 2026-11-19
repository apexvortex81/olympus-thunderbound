package com.Fowvite0.Koqfam.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.theme.BoltIndigoLight
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.SurfaceRaised
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.theme.TextTertiary

@Composable
fun PauseDialog(
    heightMeters: Int,
    relics: Int,
    seed: Long,
    onResume: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = RoundedCornerShape(22.dp),
        containerColor = SurfaceRaised,
        titleContentColor = BoltWhite,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.headlineSmall,
                color = CeremonialGold
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.pause_body, heightMeters, relics),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.seed_label, seed),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.resume),
                    style = MaterialTheme.typography.labelMedium,
                    color = BoltIndigoLight
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
            }
        }
    )
}
