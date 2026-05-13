package com.mrrobot.aiworkspace.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mrrobot.aiworkspace.data.AppThemeMode

/**
 * Root Material 3 theme for the Mr. Robot workspace.
 *
 * Highlights:
 *
 *   - Dynamic Color on Android 12+ (Material You) for Auto/Dark/Light,
 *     with a full warm brand palette fallback on older OS versions.
 *   - Cyberpunk and Hacker themes always use their bespoke palettes so
 *     their identity is preserved regardless of Material You wallpaper.
 *   - Fully populated ColorScheme tokens (primary/secondary/tertiary
 *     containers, surfaceContainer*, inverseSurface, outlineVariant,
 *     scrim, surfaceTint) defined in [Color.kt].
 *   - M3 type scale in [AppTypography] (Type.kt).
 *   - M3 shape scale in [AppShapes]/[HackerShapes] (Shape.kt).
 *
 * See https://developer.android.com/develop/ui/compose/designsystems/material3
 */
@Composable
fun MrRobotTheme(
    themeMode: AppThemeMode = AppThemeMode.Auto,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val scheme: ColorScheme = when (themeMode) {
        AppThemeMode.Auto -> when {
            supportsDynamic && systemDark -> dynamicDarkColorScheme(context)
            supportsDynamic -> dynamicLightColorScheme(context)
            systemDark -> WarmDarkScheme
            else -> WarmLightScheme
        }

        AppThemeMode.Dark -> when {
            supportsDynamic -> dynamicDarkColorScheme(context)
            else -> WarmDarkScheme
        }

        AppThemeMode.Light -> when {
            supportsDynamic -> dynamicLightColorScheme(context)
            else -> WarmLightScheme
        }

        // Branded palettes always win over Material You so the workspace
        // keeps its cyber/hacker identity regardless of wallpaper tones.
        AppThemeMode.Cyberpunk -> CyberLightScheme
        AppThemeMode.Hacker -> HackerDarkScheme
    }

    val shapes = when (themeMode) {
        AppThemeMode.Hacker -> HackerShapes
        else -> AppShapes
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = AppTypography,
        shapes = shapes,
        content = content
    )
}

/**
 * Returns true when the currently selected [AppThemeMode] renders the UI
 * with a dark surface, accounting for the system theme when in Auto mode.
 *
 * Used by [com.mrrobot.aiworkspace.MainActivity] to tint system bars.
 */
fun AppThemeMode.isDark(systemDark: Boolean): Boolean = when (this) {
    AppThemeMode.Auto -> systemDark
    AppThemeMode.Dark -> true
    AppThemeMode.Light -> false
    AppThemeMode.Cyberpunk -> false
    AppThemeMode.Hacker -> true
}
