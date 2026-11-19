package com.Fowvite0.Koqfam.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Fowvite0.Koqfam.core.theme.GlassSurface
import com.Fowvite0.Koqfam.core.theme.IndigoHairline

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = IndigoHairline,
    containerColor: Color = GlassSurface,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 14.dp,
    contentPadding: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = elevation,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}
