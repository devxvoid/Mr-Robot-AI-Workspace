package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design tokens that extend `MaterialTheme` with the semantic roles the
 * GitHub Store-inspired UI needs (glass surfaces, spacing scale,
 * elevation ladder, border tints).
 *
 * Keeping these as plain data (not Compositions) lets us read them
 * anywhere inside a composable without extra wiring.
 */

/** Spacing scale used across padding, gaps, and component insets. */
object Spacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val s: Dp = 8.dp
    val m: Dp = 12.dp
    val l: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val huge: Dp = 48.dp

    /** Horizontal screen edge padding for the storefront layout. */
    val screenEdge: Dp = 18.dp

    /** Vertical rhythm between major sections on a screen. */
    val sectionGap: Dp = 22.dp
}

/** Elevation ladder — matches the GitHub Store depth feel. */
object Elevation {
    val none: Dp = 0.dp
    val level1: Dp = 1.dp
    val level2: Dp = 3.dp
    val level3: Dp = 6.dp
    val level4: Dp = 10.dp
    val featured: Dp = 14.dp
}

/**
 * Glass surface roles — these are alpha overlays applied on top of
 * `surfaceContainer*` colors to get the translucent-glass feel without
 * relying on real blur (which has narrow API level support).
 */
object GlassTokens {
    const val RestingAlpha: Float = 0.72f
    const val ElevatedAlpha: Float = 0.82f
    const val FloatingAlpha: Float = 0.92f
    const val BannerFillAlpha: Float = 0.55f
    const val HairlineAlpha: Float = 0.38f
    const val GlowAlpha: Float = 0.18f
}

/**
 * Semantic color accessors. These are deliberately lightweight wrappers
 * around `MaterialTheme.colorScheme` so call sites stay readable:
 *
 *   background color = StoreColors.background()
 *   glass surface    = StoreColors.glassSurface()
 *
 * The wrappers only live as composables to access the active theme.
 */
object StoreColors {
    @Composable
    @ReadOnlyComposable
    fun background(): Color = MaterialTheme.colorScheme.background

    @Composable
    @ReadOnlyComposable
    fun primarySurface(): Color = MaterialTheme.colorScheme.surfaceContainer

    @Composable
    @ReadOnlyComposable
    fun secondarySurface(): Color = MaterialTheme.colorScheme.surfaceContainerLow

    @Composable
    @ReadOnlyComposable
    fun tertiarySurface(): Color = MaterialTheme.colorScheme.surfaceContainerLowest

    @Composable
    @ReadOnlyComposable
    fun elevatedSurface(): Color = MaterialTheme.colorScheme.surfaceContainerHigh

    @Composable
    @ReadOnlyComposable
    fun glassSurface(): Color =
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = GlassTokens.RestingAlpha)

    @Composable
    @ReadOnlyComposable
    fun glassSurfaceElevated(): Color =
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = GlassTokens.ElevatedAlpha)

    @Composable
    @ReadOnlyComposable
    fun glassBorder(): Color =
        MaterialTheme.colorScheme.outline.copy(alpha = GlassTokens.HairlineAlpha)

    @Composable
    @ReadOnlyComposable
    fun glowTint(): Color =
        MaterialTheme.colorScheme.primary.copy(alpha = GlassTokens.GlowAlpha)

    @Composable
    @ReadOnlyComposable
    fun subtleDivider(): Color = MaterialTheme.colorScheme.outlineVariant

    @Composable
    @ReadOnlyComposable
    fun mutedText(): Color = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    @ReadOnlyComposable
    fun primaryText(): Color = MaterialTheme.colorScheme.onSurface
}

/**
 * Static helpers for cases where we need a color without being inside a
 * composable (e.g., window insets configuration in MainActivity).
 */
fun ColorScheme.storeBackgroundBase(): Color = background
fun ColorScheme.storeSurfaceBase(): Color = surfaceContainerLow
