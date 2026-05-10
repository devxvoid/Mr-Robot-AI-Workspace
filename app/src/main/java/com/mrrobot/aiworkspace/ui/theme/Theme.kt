package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrrobot.aiworkspace.data.AppThemeMode

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF00DFF7),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFF22C55E),
    background = Color(0xFF030712),
    surface = Color(0xFF0B1020),
    surfaceVariant = Color(0xFF111827),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFFB8C0CC),
    outline = Color(0xFF334155)
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    secondary = Color(0xFF6D28D9),
    tertiary = Color(0xFF15803D),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEFF6FF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

private val CyberpunkScheme = lightColorScheme(
    primary = Color(0xFF000000),
    secondary = Color(0xFF0058BE),
    tertiary = Color(0xFF10B981),
    background = Color(0xFFFCF8FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE4E2E4),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1B1B1D),
    onSurface = Color(0xFF1B1B1D),
    onSurfaceVariant = Color(0xFF45464D),
    outline = Color(0xFF76777D)
)

private val HackerScheme = darkColorScheme(
    primary = Color(0xFFFFB3B6),
    secondary = Color(0xFFFFB4AC),
    tertiary = Color(0xFFFFB95F),
    background = Color(0xFF131313),
    surface = Color(0xFF1C1B1B),
    surfaceVariant = Color(0xFF353534),
    primaryContainer = Color(0xFFE11D48),
    secondaryContainer = Color(0xFF921517),
    tertiaryContainer = Color(0xFFA36700),
    onPrimary = Color(0xFF68001A),
    onSecondary = Color(0xFF690007),
    onTertiary = Color(0xFF472A00),
    onBackground = Color(0xFFE5E2E1),
    onSurface = Color(0xFFE5E2E1),
    onSurfaceVariant = Color(0xFFE5BDBE),
    outline = Color(0xFFAC8889),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val RoundedShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

private val CyberpunkShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

private val HackerShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 52.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    )
)

@Composable
fun MrRobotTheme(
    themeMode: AppThemeMode = AppThemeMode.Auto,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()

    val scheme = when (themeMode) {
        AppThemeMode.Auto -> if (systemDark) DarkScheme else LightScheme
        AppThemeMode.Dark -> DarkScheme
        AppThemeMode.Light -> LightScheme
        AppThemeMode.Cyberpunk -> CyberpunkScheme
        AppThemeMode.Hacker -> HackerScheme
    }

    val shapes = when (themeMode) {
        AppThemeMode.Hacker -> HackerShapes
        AppThemeMode.Cyberpunk -> CyberpunkShapes
        else -> RoundedShapes
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = AppTypography,
        shapes = shapes,
        content = content
    )
}
