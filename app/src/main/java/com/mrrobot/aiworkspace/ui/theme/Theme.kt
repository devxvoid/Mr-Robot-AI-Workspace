package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.mrrobot.aiworkspace.data.AppThemeMode

/**
 * `MrRobotTheme` — GitHub Store inspired theme entry point.
 *
 * AppThemeMode is preserved so persisted user preferences keep working,
 * but each mode now maps to a premium store-style palette:
 *
 *  - Auto        → Ocean Blue light / Ocean Blue dark (follows system)
 *  - Light       → Ocean Blue light
 *  - Dark        → Ocean Blue dark
 *  - Cyberpunk   → Deep Purple dark (expressive accent heavy)
 *  - Hacker      → Slate AMOLED dark (true-black terminal vibe)
 *
 * Shapes default to the expressive 32.dp store shape scale; the
 * Hacker mode swaps in a squared terminal scale.
 */
@Composable
fun MrRobotTheme(
    themeMode: AppThemeMode = AppThemeMode.Auto,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()

    val scheme: ColorScheme = when (themeMode) {
        AppThemeMode.Auto -> if (systemDark) oceanBlueDark else oceanBlueLight
        AppThemeMode.Light -> oceanBlueLight
        AppThemeMode.Dark -> oceanBlueDark
        AppThemeMode.Cyberpunk -> deepPurpleDark
        AppThemeMode.Hacker -> slateGrayDark.toAmoled()
    }

    val shapes = when (themeMode) {
        AppThemeMode.Hacker -> TerminalShapes
        else -> StoreShapes
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = StoreTypography,
        shapes = shapes,
        content = content
    )
}
