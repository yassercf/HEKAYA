package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = HekayaBlue,
    onPrimary = Color.White,
    primaryContainer = HekayaLightBlue,
    onPrimaryContainer = HekayaDarkBlue,
    secondary = HekayaNavy,
    onSecondary = Color.White,
    secondaryContainer = HekayaBadgeBg,
    onSecondaryContainer = HekayaBadgeText,
    tertiary = HekayaGold,
    onTertiary = Color.White,
    tertiaryContainer = HekayaGoldLight,
    onTertiaryContainer = Color(0xFF78350F),
    background = HekayaSurfaceBlue,
    onBackground = HekayaTextPrimary,
    surface = Color.White,
    onSurface = HekayaTextPrimary,
    surfaceVariant = HekayaInputBg,
    onSurfaceVariant = HekayaTextSecondary,
    outline = HekayaBorder,
    outlineVariant = HekayaDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF93C5FD),
    onSecondary = Color(0xFF0F172A),
    background = Color(0xFF0B132B),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1C2541),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF243356),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155)
)

@Composable
fun HekayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
