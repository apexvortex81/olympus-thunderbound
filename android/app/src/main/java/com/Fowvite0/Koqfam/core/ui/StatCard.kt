package com.Fowvite0.Koqfam.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Fowvite0.Koqfam.core.theme.RaisedGlass
import com.Fowvite0.Koqfam.core.theme.TextSecondary

data class StatEntry(
    val labelRes: Int,
    val value: String,
    val accent: Color,
    val visible: Boolean
)

@Composable
fun StatCard(
    labelRes: Int,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    if (value.isBlank()) return
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = RaisedGlass,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .background(accent, CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = accent,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun StatRow(
    entries: List<StatEntry>,
    modifier: Modifier = Modifier
) {
    val visible = entries.filter { it.visible && it.value.isNotBlank() }
    if (visible.size < 2) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        visible.forEach { entry ->
            StatCard(
                labelRes = entry.labelRes,
                value = entry.value,
                accent = entry.accent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
