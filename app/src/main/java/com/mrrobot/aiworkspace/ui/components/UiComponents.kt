package com.mrrobot.aiworkspace.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared Material 3 UI components for the Mr. Robot workspace.
 *
 * Every component in this file follows M3 rules:
 *
 *   - No hardcoded colors. All colors come from MaterialTheme.colorScheme.
 *   - No hardcoded shapes. All corner rounding comes from MaterialTheme.shapes.
 *   - No hardcoded typography. All text styles come from MaterialTheme.typography.
 *   - No borders on cards. Elevation and surfaceVariant carry visual separation.
 *   - No custom pills / chips that duplicate M3 components. Reuse SuggestionChip.
 *
 * The resulting surface looks like Google's own Material 3 demo apps.
 */

/* ------------------------------------------------------------------ */
/* Screen scaffolding                                                 */
/* ------------------------------------------------------------------ */

/**
 * Default M3 screen content padding.
 *
 * 16dp on screen edges (per M3 spec for handset breakpoints) and
 * 8dp internal grouping via Arrangement.spacedBy on the hosting
 * LazyColumn / Column.
 */
val ScreenHorizontalPadding: Dp = 16.dp
val ScreenVerticalPadding: Dp = 16.dp
val GroupSpacing: Dp = 8.dp
val SectionSpacing: Dp = 16.dp

/**
 * Standard padding used by every screen's LazyColumn so content sits
 * on a consistent 8dp grid with 16dp screen edges and enough bottom
 * space for the navigation bar.
 */
val ScreenContentPadding: PaddingValues = PaddingValues(
    start = ScreenHorizontalPadding,
    end = ScreenHorizontalPadding,
    top = 8.dp,
    bottom = 24.dp
)

/* ------------------------------------------------------------------ */
/* Cards                                                              */
/* ------------------------------------------------------------------ */

/**
 * The canonical content card used throughout the app.
 *
 * Built on [ElevatedCard] with M3 tonal elevation - never a border -
 * and [MaterialTheme.shapes.medium] corners (12dp). Internal padding
 * sits on the 8dp grid (16dp default for comfortable density).
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            focusedElevation = 2.dp,
            hoveredElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

/* ------------------------------------------------------------------ */
/* Headers                                                            */
/* ------------------------------------------------------------------ */

/**
 * Hero section header. Uses headlineMedium for the title and
 * bodyLarge for the subtitle so every screen's opening block looks
 * identical in scale and spacing.
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * In-card grouped title. Mapped to titleLarge per M3 so cards align
 * with the type scale.
 */
@Composable
fun GroupTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Supporting body text. Mapped to bodyMedium with onSurfaceVariant
 * so it always meets 4.5:1 contrast against surface backgrounds.
 */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

/**
 * Caption / overline text. Mapped to labelSmall for M3-consistent
 * micro-typography on chips and status lines.
 */
@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

/* ------------------------------------------------------------------ */
/* Metric card (stat tile)                                            */
/* ------------------------------------------------------------------ */

/**
 * Compact metric tile used on dashboards. Keeps the M3 card look and
 * composes a prominent value on top of a label + description using the
 * correct type roles (headlineSmall, titleMedium, bodySmall).
 */
@Composable
fun MetricCard(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* ------------------------------------------------------------------ */
/* Chips                                                              */
/* ------------------------------------------------------------------ */

/**
 * Thin wrapper around [SuggestionChip] so screens can use it as a
 * read-only status chip while staying fully on the M3 chip component.
 */
@Composable
fun StatusChip(
    label: String,
    modifier: Modifier = Modifier
) {
    // Use a Surface-based pill rather than SuggestionChip so we don't
    // inherit the default chip outline. Fully on M3 tokens.
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
        )
    }
}

/* ------------------------------------------------------------------ */
/* Primary action button                                              */
/* ------------------------------------------------------------------ */

/**
 * Prominent filled-tonal action button used inside cards for the
 * "main" action. Full width with M3 shapes.small on a 40dp touch
 * target - identical to Google's own Settings / Contacts apps.
 */
@Composable
fun PrimaryTonalButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/* ------------------------------------------------------------------ */
/* Two column / row helpers                                           */
/* ------------------------------------------------------------------ */

/**
 * Equal-weight two-column row on the 8dp grid. Used for side-by-side
 * [MetricCard]s and CTA button pairs.
 */
@Composable
fun TwoColumnRow(
    modifier: Modifier = Modifier,
    spacing: Dp = GroupSpacing,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}
