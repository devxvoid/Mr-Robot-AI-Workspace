package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mrrobot.aiworkspace.data.AppThemeMode

val CyberBlack = Color(0xFF030712)
val CyberSurface = Color(0xFF0B1020)
val CyberPanel = Color(0xFF111827)
val CyberCyan = Color(0xFF00DFF7)
val CyberPurple = Color(0xFF8B5CF6)
val CyberGreen = Color(0xFF22C55E)
val CyberText = Color(0xFFE5E7EB)
val CyberMuted = Color(0xFF9CA3AF)

val LightBg = Color(0xFFF7FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightPanel = Color(0xFFEFF6FF)
val LightText = Color(0xFF0F172A)
val LightMuted = Color(0xFF475569)

private val DarkScheme = darkColorScheme(
    primary = CyberCyan,
    secondary = CyberPurple,
    tertiary = CyberGreen,
    background = CyberBlack,
    surface = CyberSurface,
    surfaceVariant = CyberPanel,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = CyberText,
    onSurface = CyberText,
    onSurfaceVariant = CyberMuted
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    secondary = Color(0xFF6D28D9),
    tertiary = Color(0xFF15803D),
    background = LightBg,
    surface = LightSurface,
    surfaceVariant = LightPanel,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = LightMuted
)

@Composable
fun MrRobotTheme(
    themeMode: AppThemeMode = AppThemeMode.Auto,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        AppThemeMode.Auto -> isSystemInDarkTheme()
        AppThemeMode.Dark -> true
        AppThemeMode.Light -> false
    }

    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        content = content
    )
}
