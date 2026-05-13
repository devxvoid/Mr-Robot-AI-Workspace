package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Full Material 3 tonal palettes with every ColorScheme token defined.
 *
 * Each role is provided explicitly (primary/onPrimary/primaryContainer/onPrimaryContainer
 * and all surface, surfaceContainer* and inverse tokens) so that M3 components render
 * with correct semantic roles and 4.5:1 contrast ratios on text.
 *
 * See https://m3.material.io/styles/color/the-color-system/tokens
 */

/* ------------------------------------------------------------------ */
/* Warm palette (default brand)                                       */
/* ------------------------------------------------------------------ */

internal val WarmLightScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF8A4B2A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC9),
    onPrimaryContainer = Color(0xFF331100),

    secondary = Color(0xFF76574A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCC),
    onSecondaryContainer = Color(0xFF2C160B),

    tertiary = Color(0xFF0E7C71),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFA7F3EA),
    onTertiaryContainer = Color(0xFF00201C),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFFF8F4),
    onBackground = Color(0xFF201A16),
    surface = Color(0xFFFFF8F4),
    onSurface = Color(0xFF201A16),

    surfaceVariant = Color(0xFFF3DFD1),
    onSurfaceVariant = Color(0xFF52443B),

    surfaceTint = Color(0xFF8A4B2A),
    inverseSurface = Color(0xFF362F2A),
    inverseOnSurface = Color(0xFFFBEEE6),
    inversePrimary = Color(0xFFFFB68F),

    outline = Color(0xFF847369),
    outlineVariant = Color(0xFFD6C3B6),
    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFFFFF8F4),
    surfaceDim = Color(0xFFE4D8CE),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBEEE4),
    surfaceContainer = Color(0xFFF5E8DE),
    surfaceContainerHigh = Color(0xFFEFE2D8),
    surfaceContainerHighest = Color(0xFFE9DCD2)
)

internal val WarmDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFFFB68F),
    onPrimary = Color(0xFF502100),
    primaryContainer = Color(0xFF6C3315),
    onPrimaryContainer = Color(0xFFFFDBC9),

    secondary = Color(0xFFE6BEA9),
    onSecondary = Color(0xFF432A1D),
    secondaryContainer = Color(0xFF5C4033),
    onSecondaryContainer = Color(0xFFFFDBCC),

    tertiary = Color(0xFF8BD7CD),
    onTertiary = Color(0xFF00382F),
    tertiaryContainer = Color(0xFF005148),
    onTertiaryContainer = Color(0xFFA7F3EA),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF17120F),
    onBackground = Color(0xFFECDED3),
    surface = Color(0xFF17120F),
    onSurface = Color(0xFFECDED3),

    surfaceVariant = Color(0xFF52443B),
    onSurfaceVariant = Color(0xFFD6C3B6),

    surfaceTint = Color(0xFFFFB68F),
    inverseSurface = Color(0xFFECDED3),
    inverseOnSurface = Color(0xFF362F2A),
    inversePrimary = Color(0xFF8A4B2A),

    outline = Color(0xFF9F8D82),
    outlineVariant = Color(0xFF52443B),
    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFF3F3833),
    surfaceDim = Color(0xFF17120F),
    surfaceContainerLowest = Color(0xFF110D0A),
    surfaceContainerLow = Color(0xFF201A16),
    surfaceContainer = Color(0xFF241E1A),
    surfaceContainerHigh = Color(0xFF2F2824),
    surfaceContainerHighest = Color(0xFF3A332E)
)

/* ------------------------------------------------------------------ */
/* Cyberpunk palette (a cool teal/purple light-biased scheme)         */
/* ------------------------------------------------------------------ */

internal val CyberLightScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF00677B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2EBFF),
    onPrimaryContainer = Color(0xFF001F26),

    secondary = Color(0xFF6750A4),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),

    tertiary = Color(0xFF0E7C71),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFA7F3EA),
    onTertiaryContainer = Color(0xFF00201C),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF7FAFD),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFF7FAFD),
    onSurface = Color(0xFF0F172A),

    surfaceVariant = Color(0xFFDDE3EC),
    onSurfaceVariant = Color(0xFF41484F),

    surfaceTint = Color(0xFF00677B),
    inverseSurface = Color(0xFF2C3137),
    inverseOnSurface = Color(0xFFEDF1F6),
    inversePrimary = Color(0xFF58D6F0),

    outline = Color(0xFF71787F),
    outlineVariant = Color(0xFFC1C7CF),
    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFFF7FAFD),
    surfaceDim = Color(0xFFD7DBE0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F4F8),
    surfaceContainer = Color(0xFFEBEEF2),
    surfaceContainerHigh = Color(0xFFE5E9ED),
    surfaceContainerHighest = Color(0xFFDFE3E7)
)

/* ------------------------------------------------------------------ */
/* Hacker palette (red-on-black high-contrast)                        */
/* ------------------------------------------------------------------ */

internal val HackerDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B6E),
    onPrimary = Color(0xFF2E0003),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),

    secondary = Color(0xFFFFB4AC),
    onSecondary = Color(0xFF561D17),
    secondaryContainer = Color(0xFF73332B),
    onSecondaryContainer = Color(0xFFFFDAD3),

    tertiary = Color(0xFFFFBA74),
    onTertiary = Color(0xFF482900),
    tertiaryContainer = Color(0xFF663C00),
    onTertiaryContainer = Color(0xFFFFDDB9),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF0B0B0B),
    onBackground = Color(0xFFE5E2E1),
    surface = Color(0xFF0B0B0B),
    onSurface = Color(0xFFE5E2E1),

    surfaceVariant = Color(0xFF3A2F30),
    onSurfaceVariant = Color(0xFFE5BDBE),

    surfaceTint = Color(0xFFFF6B6E),
    inverseSurface = Color(0xFFE5E2E1),
    inverseOnSurface = Color(0xFF1C1B1B),
    inversePrimary = Color(0xFFBC000B),

    outline = Color(0xFFAC8889),
    outlineVariant = Color(0xFF5A3F40),
    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFF2F2828),
    surfaceDim = Color(0xFF0B0B0B),
    surfaceContainerLowest = Color(0xFF050505),
    surfaceContainerLow = Color(0xFF131313),
    surfaceContainer = Color(0xFF1A1818),
    surfaceContainerHigh = Color(0xFF241F20),
    surfaceContainerHighest = Color(0xFF2E2828)
)
