package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CyberBlack = Color(0xFF030712)
val CyberSurface = Color(0xFF0B1020)
val CyberPanel = Color(0xFF111827)
val CyberCyan = Color(0xFF00E5FF)
val CyberPurple = Color(0xFF8B5CF6)
val CyberGreen = Color(0xFF22C55E)
val CyberText = Color(0xFFE5E7EB)
val CyberMuted = Color(0xFF9CA3AF)

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

@Composable
fun MrRobotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        content = content
    )
}
