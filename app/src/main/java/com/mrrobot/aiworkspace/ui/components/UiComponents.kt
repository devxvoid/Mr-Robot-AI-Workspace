package com.mrrobot.aiworkspace.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.theme.Elevation
import com.mrrobot.aiworkspace.ui.theme.GlassTokens
import com.mrrobot.aiworkspace.ui.theme.Spacing
import com.mrrobot.aiworkspace.ui.theme.StoreColors

/* ============================================================
 *  Legacy color constants — kept so older screens compile.
 *  Prefer MaterialTheme.colorScheme / StoreColors for new code.
 * ============================================================ */
val NeonCyan = Color(0xFF00DFF7)
val NeonPurple = Color(0xFF8B5CF6)
val NeonGreen = Color(0xFF22C55E)
val DeepBg = Color(0xFF030712)
val Panel = Color(0xFF111827)
val PanelSoft = Color(0xFF0B1020)
val SoftText = Color(0xFFB8C0CC)
val Danger = Color(0xFFFF6B6B)

/* ============================================================
 *  Screen shell & ambient background (the "store" atmosphere).
 * ============================================================ */

/**
 * The standard app-wide screen scaffold used by every top-level screen.
 *
 * Provides:
 * - a vertical gradient from background → surfaceContainerLow →
 *   surfaceContainerHigh (premium dark depth without looking flat).
 * - two soft radial glows (primary + tertiary) that gently pulse,
 *   delivering the "liquid glass" atmosphere without real blur.
 * - consistent screen-edge padding.
 */
@Composable
fun ScreenShell(
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Spacing.screenEdge,
        vertical = Spacing.l
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to scheme.background,
                    0.45f to scheme.surfaceContainerLow,
                    1.0f to scheme.surfaceContainerHigh
                )
            )
    ) {
        AmbientGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun AmbientGlow(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "ambient_glow")

    val alpha by infinite.animateFloat(
        initialValue = GlassTokens.GlowAlpha * 0.55f,
        targetValue = GlassTokens.GlowAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_alpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-110).dp, y = (-40).dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 130.dp, y = 100.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/* ============================================================
 *  Liquid glass surfaces.
 * ============================================================ */

/**
 * The primary content container. Translucent surface, hairline outline,
 * expressive 24.dp corners — same feel as GitHub Store's ExpressiveCard.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = Spacing.xl,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.large
    val container = StoreColors.glassSurface()
    val border = StoreColors.glassBorder()

    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, border, shape),
            shape = shape,
            colors = CardDefaults.elevatedCardColors(containerColor = container),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = Elevation.level2,
                pressedElevation = Elevation.level3
            )
        ) {
            Column(
                modifier = Modifier.padding(padding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, border, shape),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = container),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level2)
        ) {
            Column(
                modifier = Modifier.padding(padding),
                content = content
            )
        }
    }
}

/**
 * Higher-emphasis glass surface. Used for hero banners and featured
 * content. Adds a subtle inner glow via a radial gradient underlay.
 */
@Composable
fun ElevatedGlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = Spacing.xl,
    glow: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.extraLarge
    val container = StoreColors.glassSurfaceElevated()
    val border = StoreColors.glassBorder()
    val glowTint = StoreColors.glowTint()

    val baseModifier = modifier
        .fillMaxWidth()
        .border(1.dp, border, shape)

    val cardModifier = if (onClick != null) baseModifier.clickable(onClick = onClick) else baseModifier

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level3)
    ) {
        Box {
            if (glow) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .offset(x = (-60).dp, y = (-60).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(glowTint, Color.Transparent)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    }
}

/**
 * `glassSurface()` modifier — for one-off translucent containers that
 * aren't a full Card (e.g., the composer pill, bottom nav, filter bars).
 */
fun Modifier.glassSurface(
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(24.dp),
    alpha: Float = GlassTokens.ElevatedAlpha,
    borderColor: Color? = null
): Modifier = this
    .clip(shape)
    .then(
        if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier
    )

/* ============================================================
 *  Typography helpers (kept for back-compat with existing screens).
 * ============================================================ */

@Composable
fun PremiumHeader(
    title: String,
    subtitle: String,
    @Suppress("UNUSED_PARAMETER") badge: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(Spacing.s))

        Text(
            text = subtitle,
            color = StoreColors.mutedText(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        color = StoreColors.mutedText(),
        style = MaterialTheme.typography.bodyMedium
    )
}

/* ============================================================
 *  Store-grade reusable components.
 * ============================================================ */

/** Section heading used across the storefront. Optional trailing action. */
@Composable
fun SectionHeader(
    title: String,
    trailingAction: String? = null,
    onTrailingClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        if (trailingAction != null && onTrailingClick != null) {
            Text(
                text = trailingAction,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onTrailingClick)
            )
        }
    }
}

/** Translucent top app bar used at the top of storefront-style screens. */
@Composable
fun TranslucentTopBar(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        color = StoreColors.glassSurface(),
        tonalElevation = Elevation.level2,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenEdge, vertical = Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(Spacing.m))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = StoreColors.mutedText(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (trailing != null) {
                Spacer(Modifier.width(Spacing.s))
                trailing()
            }
        }
    }
}

/**
 * Featured banner — hero element at the top of the store page.
 * Uses an elevated glass card with a diagonal gradient wash that
 * pulls from primary/tertiary, matching the GitHub Store featured tile.
 */
@Composable
fun FeaturedBanner(
    eyebrow: String,
    title: String,
    body: String,
    actionText: String,
    onActionClick: () -> Unit,
    icon: ImageVector? = null
) {
    val scheme = MaterialTheme.colorScheme

    ElevatedGlassCard(padding = Spacing.xxl) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                IconBadge(
                    icon = icon,
                    tint = scheme.primary,
                    background = scheme.primaryContainer.copy(alpha = 0.35f)
                )
                Spacer(Modifier.width(Spacing.m))
            }

            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = scheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(Spacing.s))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(Spacing.s))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = StoreColors.mutedText(),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(Spacing.l))

        StoreButton(
            text = actionText,
            onClick = onActionClick,
            style = StoreButtonStyle.Filled
        )
    }
}

/**
 * Store tile — grid tile used for quick actions and curated sections.
 * Matches the GitHub Store discovery card rhythm.
 */
@Composable
fun StoreTile(
    title: String,
    description: String,
    icon: ImageVector? = null,
    tint: Color? = null,
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val accent = tint ?: scheme.primary

    GlassCard(
        modifier = modifier,
        onClick = onClick,
        padding = Spacing.l
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                IconBadge(
                    icon = icon,
                    tint = accent,
                    background = accent.copy(alpha = 0.14f)
                )
                Spacer(Modifier.width(Spacing.m))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (badge != null) {
                        Spacer(Modifier.width(Spacing.s))
                        StatBadge(text = badge, tint = accent)
                    }
                }

                Spacer(Modifier.height(Spacing.xs))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = StoreColors.mutedText(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Circular icon badge with tinted translucent background. */
@Composable
fun IconBadge(
    icon: ImageVector,
    tint: Color,
    background: Color,
    size: Dp = 44.dp,
    iconSize: Dp = size * 0.5f
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/** Painter-resource variant of [IconBadge] for drawable-xml icons. */
@Composable
fun IconBadge(
    painter: androidx.compose.ui.graphics.painter.Painter,
    tint: Color,
    background: Color,
    size: Dp = 44.dp,
    iconSize: Dp = size * 0.5f
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/** Compact pill used inline with a title (e.g., "New", "3"). */
@Composable
fun StatBadge(
    text: String,
    tint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = tint.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.45f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/** Filter / selection chip with selected and unselected states. */
@Composable
fun StoreFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    val container = if (selected) {
        scheme.primaryContainer
    } else {
        StoreColors.glassSurface()
    }

    val contentColor = if (selected) {
        scheme.onPrimaryContainer
    } else {
        scheme.onSurface
    }

    val borderColor = if (selected) {
        scheme.primary.copy(alpha = 0.6f)
    } else {
        StoreColors.glassBorder()
    }

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = container,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

/** Pill-shaped translucent search field. */
@Composable
fun StoreSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search the store",
    leading: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        color = StoreColors.glassSurface(),
        border = BorderStroke(1.dp, StoreColors.glassBorder())
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                Icon(
                    imageVector = leading,
                    contentDescription = null,
                    tint = StoreColors.mutedText(),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(Spacing.m))
            }

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = scheme.onSurface
                    ),
                    cursorBrush = SolidColor(scheme.primary),
                    decorationBox = { innerField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = StoreColors.mutedText()
                            )
                        }
                        innerField()
                    }
                )
            }

            if (trailing != null) {
                Spacer(Modifier.width(Spacing.s))
                trailing()
            }
        }
    }
}

/** Primary button used across the store. */
enum class StoreButtonStyle {
    Filled,
    Tonal,
    Outlined
}

@Composable
fun StoreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: StoreButtonStyle = StoreButtonStyle.Filled,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val colors = when (style) {
        StoreButtonStyle.Filled -> ButtonDefaults.buttonColors()
        StoreButtonStyle.Tonal -> ButtonDefaults.filledTonalButtonColors()
        StoreButtonStyle.Outlined -> ButtonDefaults.outlinedButtonColors()
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        colors = colors,
        modifier = modifier.heightIn(min = 48.dp),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Legacy alias — existing screens call `CyberButton("text") { ... }`.
 * Routes through the new StoreButton so nothing has to change.
 */
@Composable
fun CyberButton(text: String, onClick: () -> Unit) {
    StoreButton(
        text = text,
        onClick = onClick,
        style = StoreButtonStyle.Filled,
        modifier = Modifier.fillMaxWidth()
    )
}

/** Status pill (small, inline, tinted). */
@Composable
fun StatusPill(
    text: String,
    color: Color? = null
) {
    val pillColor = color ?: MaterialTheme.colorScheme.primary

    Surface(
        color = pillColor.copy(alpha = 0.10f),
        border = BorderStroke(
            width = 1.dp,
            color = pillColor.copy(alpha = 0.38f)
        ),
        shape = CircleShape
    ) {
        Text(
            text = text,
            color = pillColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** Big headline metric surface. */
@Composable
fun PremiumMetric(
    label: String,
    value: String,
    description: String
) {
    GlassCard(padding = Spacing.l) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )

        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = description,
            color = StoreColors.mutedText(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Key/value row used inside metric panels and detail cards.
 */
@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = StoreColors.mutedText()
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
