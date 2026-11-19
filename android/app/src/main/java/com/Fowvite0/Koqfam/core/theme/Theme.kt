package com.Fowvite0.Koqfam.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StormColorScheme = darkColorScheme(
    primary = BoltIndigo,
    onPrimary = BackgroundDeep,
    primaryContainer = SurfaceRaised,
    onPrimaryContainer = BoltWhite,
    secondary = CeremonialGold,
    onSecondary = BackgroundDeep,
    secondaryContainer = SurfaceBasalt,
    onSecondaryContainer = CeremonialGold,
    tertiary = BoltIndigoLight,
    onTertiary = BackgroundDeep,
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = SurfaceStorm,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextSecondary,
    outline = IndigoHairline,
    outlineVariant = BasaltStone,
    error = DangerCrimson,
    onError = BackgroundDeep,
    scrim = BackgroundDeep
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StormColorScheme,
        typography = AppTypography,
        content = content
    )
}
