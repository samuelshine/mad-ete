package com.mad.movieexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TealGreen,
    onPrimary = Night,
    primaryContainer = SurfaceHigh,
    onPrimaryContainer = WarmText,
    secondary = MintGlow,
    onSecondary = Night,
    tertiary = ColorWhite,
    onTertiary = Night,
    background = Night,
    onBackground = WarmText,
    surface = DeepOcean,
    onSurface = WarmText,
    surfaceVariant = SurfaceMid,
    onSurfaceVariant = MutedText,
    error = Danger
)

private val LightColorScheme = lightColorScheme(
    primary = TealGreen,
    onPrimary = Night,
    background = ColorWhite,
    onBackground = Night,
    surface = ColorWhite,
    onSurface = Night,
    surfaceVariant = ColorGray,
    onSurfaceVariant = DeepOcean
)

@Composable
fun MovieExplorerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
