package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * GitHub Store-inspired color system.
 *
 * Each palette defines the full Material 3 surface hierarchy
 * (surfaceContainerLowest through surfaceContainerHighest, plus surfaceDim
 * and surfaceBright) so the liquid-glass components have a proper depth
 * ladder to draw from.
 *
 * Palettes are adapted from the OpenHub-Store/Github-Store
 * `core/presentation` theme, rebranded for Mr. Robot AI Workspace.
 */

// ---------- Ocean Blue ----------

val primaryLight = Color(0xFF2A638A)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFCBE6FF)
val onPrimaryContainerLight = Color(0xFF034B71)
val secondaryLight = Color(0xFF50606F)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFD4E4F6)
val onSecondaryContainerLight = Color(0xFF394856)
val tertiaryLight = Color(0xFF66587B)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFECDCFF)
val onTertiaryContainerLight = Color(0xFF4E4162)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFF7F9FF)
val onBackgroundLight = Color(0xFF181C20)
val surfaceLight = Color(0xFFF7F9FF)
val onSurfaceLight = Color(0xFF181C20)
val surfaceVariantLight = Color(0xFFDEE3EA)
val onSurfaceVariantLight = Color(0xFF42474D)
val outlineLight = Color(0xFF72787E)
val outlineVariantLight = Color(0xFFC1C7CE)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2D3135)
val inverseOnSurfaceLight = Color(0xFFEEF1F6)
val inversePrimaryLight = Color(0xFF98CCF9)
val surfaceDimLight = Color(0xFFD7DADF)
val surfaceBrightLight = Color(0xFFF7F9FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF1F4F9)
val surfaceContainerLight = Color(0xFFEBEEF3)
val surfaceContainerHighLight = Color(0xFFE6E8EE)
val surfaceContainerHighestLight = Color(0xFFE0E3E8)

val primaryDark = Color(0xFF98CCF9)
val onPrimaryDark = Color(0xFF003350)
val primaryContainerDark = Color(0xFF034B71)
val onPrimaryContainerDark = Color(0xFFCBE6FF)
val secondaryDark = Color(0xFFB8C8D9)
val onSecondaryDark = Color(0xFF22323F)
val secondaryContainerDark = Color(0xFF394856)
val onSecondaryContainerDark = Color(0xFFD4E4F6)
val tertiaryDark = Color(0xFFD1BFE7)
val onTertiaryDark = Color(0xFF372B4A)
val tertiaryContainerDark = Color(0xFF4E4162)
val onTertiaryContainerDark = Color(0xFFECDCFF)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF101417)
val onBackgroundDark = Color(0xFFE0E3E8)
val surfaceDark = Color(0xFF101417)
val onSurfaceDark = Color(0xFFE0E3E8)
val surfaceVariantDark = Color(0xFF42474D)
val onSurfaceVariantDark = Color(0xFFC1C7CE)
val outlineDark = Color(0xFF8C9198)
val outlineVariantDark = Color(0xFF42474D)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE0E3E8)
val inverseOnSurfaceDark = Color(0xFF2D3135)
val inversePrimaryDark = Color(0xFF2A638A)
val surfaceDimDark = Color(0xFF101417)
val surfaceBrightDark = Color(0xFF363A3E)
val surfaceContainerLowestDark = Color(0xFF0B0F12)
val surfaceContainerLowDark = Color(0xFF181C20)
val surfaceContainerDark = Color(0xFF1C2024)
val surfaceContainerHighDark = Color(0xFF272A2E)
val surfaceContainerHighestDark = Color(0xFF313539)

val oceanBlueLight: ColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight
)

val oceanBlueDark: ColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark
)

// ---------- Deep Purple (used by the "Cyberpunk" mode) ----------

val deepPurpleLight: ColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE9DDFF),
    onPrimaryContainer = Color(0xFF22005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1E192B),
    tertiary = Color(0xFF7E5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E3),
    onTertiaryContainer = Color(0xFF31101D),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1C1B1E),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1C1B1E),
    surfaceVariant = Color(0xFFE7E0EB),
    onSurfaceVariant = Color(0xFF49454E),
    outline = Color(0xFF7A757F),
    outlineVariant = Color(0xFFCAC4CF),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFCFBCFF),
    surfaceDim = Color(0xFFDED8DD),
    surfaceBright = Color(0xFFFFFBFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F2F7),
    surfaceContainer = Color(0xFFF2ECF1),
    surfaceContainerHigh = Color(0xFFECE6EB),
    surfaceContainerHighest = Color(0xFFE6E1E6)
)

val deepPurpleDark: ColorScheme = darkColorScheme(
    primary = Color(0xFFCFBCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378A),
    onPrimaryContainer = Color(0xFFE9DDFF),
    secondary = Color(0xFFCBC2DB),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF4A2532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD9E3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF141316),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF141316),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC4CF),
    outline = Color(0xFF948F99),
    outlineVariant = Color(0xFF49454E),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E6),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF6750A4),
    surfaceDim = Color(0xFF141316),
    surfaceBright = Color(0xFF3A383C),
    surfaceContainerLowest = Color(0xFF0F0E11),
    surfaceContainerLow = Color(0xFF1C1B1E),
    surfaceContainer = Color(0xFF201F22),
    surfaceContainerHigh = Color(0xFF2B292D),
    surfaceContainerHighest = Color(0xFF363438)
)

// ---------- Slate Gray (used by the AMOLED "Hacker" mode) ----------

val slateGrayDark: ColorScheme = darkColorScheme(
    primary = Color(0xFFB4C7D9),
    onPrimary = Color(0xFF1F2F3D),
    primaryContainer = Color(0xFF394654),
    onPrimaryContainer = Color(0xFFD7E3F3),
    secondary = Color(0xFFBEC6D5),
    onSecondary = Color(0xFF28323B),
    secondaryContainer = Color(0xFF3E4753),
    onSecondaryContainer = Color(0xFFDAE2F1),
    tertiary = Color(0xFFDABDE2),
    onTertiary = Color(0xFF3E2946),
    tertiaryContainer = Color(0xFF553F5D),
    onTertiaryContainer = Color(0xFFF7D9FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF111416),
    onBackground = Color(0xFFE2E2E5),
    surface = Color(0xFF111416),
    onSurface = Color(0xFFE2E2E5),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CD),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE2E2E5),
    inverseOnSurface = Color(0xFF2E3133),
    inversePrimary = Color(0xFF535E6C),
    surfaceDim = Color(0xFF111416),
    surfaceBright = Color(0xFF37393B),
    surfaceContainerLowest = Color(0xFF0C0F11),
    surfaceContainerLow = Color(0xFF191C1E),
    surfaceContainer = Color(0xFF1D2022),
    surfaceContainerHigh = Color(0xFF282A2D),
    surfaceContainerHighest = Color(0xFF333538)
)

/**
 * AMOLED transform — pushes the background and lowest surfaces to pure
 * black while keeping surfaceContainer levels dark-neutral. Matches the
 * GitHub Store reference AMOLED variant.
 */
fun ColorScheme.toAmoled(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainer = Color(0xFF0A0A0A),
    surfaceContainerLow = Color(0xFF050505),
    surfaceContainerLowest = Color.Black,
    surfaceContainerHigh = Color(0xFF121212),
    surfaceContainerHighest = Color(0xFF1A1A1A),
    surfaceDim = Color(0xFF0D0D0D),
    surfaceBright = Color(0xFF1F1F1F),
    surfaceVariant = Color(0xFF121212)
)
