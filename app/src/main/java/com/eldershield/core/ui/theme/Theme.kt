package com.eldershield.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ========================================================================
// CYBERPUNK 2077 THEME SPECIFICATION
// Neon Glitch Cyan, Electric Yellow, Laser Pink & Deep Void Black
// ========================================================================

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBlack,
    primaryContainer = Color(0xFF003840),
    onPrimaryContainer = NeonCyan,
    secondary = NeonYellow,
    onSecondary = CyberBlack,
    secondaryContainer = Color(0xFF333000),
    onSecondaryContainer = NeonYellow,
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = CyberBlack,
    onBackground = CyberTextPrimary,
    surface = CyberDarkSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardSurface,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberBorder,
    error = NeonPink,
    onError = Color.White
)

private val CyberpunkLightColorScheme = lightColorScheme(
    primary = Color(0xFF006677),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F8FF),
    onPrimaryContainer = Color(0xFF002026),
    secondary = Color(0xFF8A7700),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3A3),
    onSecondaryContainer = Color(0xFF2B2500),
    tertiary = Color(0xFFB0003A),
    onTertiary = Color.White,
    background = CyberLightBackground,
    onBackground = CyberLightTextPrimary,
    surface = CyberLightSurface,
    onSurface = CyberLightTextPrimary,
    surfaceVariant = CyberLightSurfaceVariant,
    onSurfaceVariant = CyberLightTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = StatusCritical,
    onError = Color.White
)

@Composable
fun ElderShieldTheme(
    darkTheme: Boolean = true, // Default to Cyberpunk Dark
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CyberpunkDarkColorScheme else CyberpunkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ElderShieldTypography,
        content = content
    )
}
