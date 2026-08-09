package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmberOrange,
    onPrimary = Color.Black,
    primaryContainer = AmberOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = TechCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = Color.White,
    tertiary = VioletPurple,
    onTertiary = Color.White,
    background = CarbonDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderDark,
    error = CrimsonRed
)

private val LightColorScheme = darkColorScheme(
    primary = AmberOrange,
    onPrimary = Color.Black,
    primaryContainer = AmberOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = TechCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = Color.White,
    tertiary = VioletPurple,
    onTertiary = Color.White,
    background = CarbonDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderDark,
    error = CrimsonRed
)

@Composable
fun MotoCraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
