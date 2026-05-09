package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mrrobot.aiworkspace.data.AppThemeMode

val MrGreen = Color(0xFF00FF88)
val MrCyan = Color(0xFF00D4FF)
val MrPurple = Color(0xFF8B5CF6)
val MrRed = Color(0xFFFF3366)
val MrAmber = Color(0xFFFFB800)

private val DarkScheme = darkColorScheme(
    primary = MrGreen,
    secondary = MrCyan,
    tertiary = MrPurple,
    background = Color(0xFF0A0A0F),
    surface = Color(0xFF111118),
    surfaceVariant = Color(0xFF16161F),
    onPrimary = Color(0xFF020617),
    onSecondary = Color(0xFF020617),
    onTertiary = Color.White,
    onBackground = Color(0xFFE8E8F0),
    onSurface = Color(0xFFE8E8F0),
    onSurfaceVariant = Color(0xFF9CA3AF),
    error = MrRed,
    onError = Color.White
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF00A050),
    secondary = Color(0xFF0077AA),
    tertiary = Color(0xFF6D28D9),
    background = Color(0xFFF5F7FB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9EEF7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0A0A0F),
    onSurface = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF475569),
    error = Color(0xFFD21F4A),
    onError = Color.White
)

@Composable
fun MrRobotTheme(
    themeMode: AppThemeMode = AppThemeMode.Auto,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.Auto -> isSystemInDarkTheme()
        AppThemeMode.Dark -> true
        AppThemeMode.Light -> false
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content = content
    )
}
